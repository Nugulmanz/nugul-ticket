package io.nugulticket.event.service;

import com.amazonaws.services.s3.AmazonS3Client;
import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.event.dto.CalenderEventResponse;
import io.nugulticket.event.dto.EventSimpleResponse;
import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.dto.createEvent.CreateEventResponse;
import io.nugulticket.event.dto.getAllEvent.GetAllEventResponse;
import io.nugulticket.event.dto.getEvent.GetEventResponse;
import io.nugulticket.event.dto.updateEvent.UpdateEventRequest;
import io.nugulticket.event.dto.updateEvent.UpdateEventResponse;
import io.nugulticket.event.entity.Event;
import io.nugulticket.event.repository.EventRepository;
import io.nugulticket.eventtime.service.EventTimeService;
import io.nugulticket.otp.service.OtpRedisService;
import io.nugulticket.s3file.S3FileService;
import io.nugulticket.search.entity.EventDocument;
import io.nugulticket.search.repository.EventSearchRepository;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final UserService userService;
    private final EventRepository eventRepository;
    private final EventTimeService eventTimeService;
    private final S3FileService s3FileService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EventSearchRepository eventSearchRepository;
    private final OtpRedisService otpRedisService;

    // S3
    private final AmazonS3Client s3Client;

    @Value("nugulticket")
    private String bucket;

    /**
     * 공연 정보를 생성하는 메서드
     * @param authUser 현재 로그인 중인 SELLER 권한인 유저 정보
     * @param eventRequest 공연 생성에 필요한 정보가 담긴 Request 객체
     * @param image 공연 프로필 이미지
     * @return 생성된 공연 정보가 담긴 Response 객체
     */
    public CreateEventResponse createEvent(AuthUser authUser, CreateEventRequest eventRequest, MultipartFile image) {
        User user = userService.getUser(authUser.getId());

        // Redis에서 OTP 인증 상태 확인 후 만료 시 DB의 상태도 false로 업데이트
        if (!otpRedisService.isOtpVerified(authUser.getId())) {
            user.expireOtpVerification();
            userService.updateUserRole(user);
            throw new ApiException(ErrorStatus.OTP_VERIFICATION_REQUIRED);
        }

        // OTP 인증 상태 확인
        if (!otpRedisService.isOtpVerified(authUser.getId())) {
            throw new ApiException(ErrorStatus.OTP_VERIFICATION_REQUIRED);
        }

        if (!user.getUserRole().equals(UserRole.SELLER)) {
            throw new ApiException(ErrorStatus.SELLER_ROLE_REQUIRED);
        }

        // 업로드한 파일의 S3 URL 주소
        String imageUrl = s3FileService.uploadFile(image, bucket);

        Event event = new Event(user,eventRequest, imageUrl);

        // MySQL에 이벤트 저장
        Event savedEvent = eventRepository.save(event);

        // Redis에 공연 이름과 ID 매핑
        redisTemplate.opsForHash().put("eventIdMap", savedEvent.getTitle(), savedEvent.getEventId().toString());

        // Elasticsearch에 이벤트 저장
        EventDocument eventDocument = convertToEventDocument(savedEvent);
        eventSearchRepository.save(eventDocument);

        eventTimeService.createEventTimes(event,
                eventRequest.getStartDate(),
                eventRequest.getEndDate(),
                LocalTime.now(),
                eventRequest.getASeatPrice(),
                eventRequest.getVipSeatCount(),
                eventRequest.getRSeatCount(),
                eventRequest.getASeatCount());

        return new CreateEventResponse(savedEvent);
    }

    /**
     * 공연 정보를 수정하는 메서드
     * @param authUser 현재 로그인 중인 SELLER 권한의 유저
     * @param eventId 수정할 공연 Id
     * @param eventRequest 공연 수정에 필요한 정보가 담긴 Request 객체
     * @return 수정된 공연 정보가 담긴 Response 객체
     */
    @Transactional
    public UpdateEventResponse updateEvent(AuthUser authUser, Long eventId, UpdateEventRequest eventRequest) {

        User user = userService.getUser(authUser.getId());

        if (!user.getUserRole().equals(UserRole.SELLER)) {
            throw new ApiException(ErrorStatus.SELLER_ROLE_REQUIRED);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorStatus.EVENT_NOT_FOUND));

        if (!event.getUser().equals(user)) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        String imageUrl = "";
        // 업데이트리퀘스트에 업데이트할 이미지가 있다면
        if (eventRequest.getImage() != null) {
            // 기존 등록된 URL 가지고 이미지 원본 이름 가져오기
            String eventImageName = s3FileService.extractFileNameFromUrl(event.getImageUrl());

            // 가져온 이미지 원본 이름으로 S3 이미지 삭제
            s3Client.deleteObject(bucket, eventImageName);

            // 업로드한 파일의 S3 URL 주소
            imageUrl = s3FileService.uploadFile(eventRequest.getImage(), bucket);
        }

        event.updateEvent(eventRequest, imageUrl);

        // MySQL에 수정된 이벤트 저장
        Event updatedEvent = eventRepository.save(event);

        // Elasticsearch에 수정된 이벤트 저장
        EventDocument eventDocument = convertToEventDocument(updatedEvent);
        eventSearchRepository.save(eventDocument);

        return new UpdateEventResponse(updatedEvent);
    }

    /**
     * 등록된 공연을 삭제하는 메서드
     * @param authUser 현재 로그인 중인 SELLER 권한의 유저 정보
     * @param eventId 삭제할 공연 ID
     */
    @Transactional
    public void deleteEvent(AuthUser authUser, Long eventId) {

        User adminUser = userService.getUser(authUser.getId());

        if (!adminUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new ApiException(ErrorStatus.ADMIN_ROLE_REQUIRED);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorStatus.EVENT_NOT_FOUND));

        event.deleteEvent();
        eventRepository.save(event);

        // 공연 검색이라 Elasticsearch 에서는 이벤트 완전 삭제
        eventSearchRepository.deleteById(eventId);
    }

    /**
     * eventId에 해당하는 공연 정보를 반환하는 메서드
     * @param eventId 조회할 eventId
     * @return 해당 eventId 정보를 포함하고 있는 공연 정보가 담긴 Response 객체
     */
    @Transactional(readOnly = true)
    public GetEventResponse getEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorStatus.EVENT_NOT_FOUND));

        return new GetEventResponse(event);
    }

    /**
     * 모든 공연 정보를 조회하는 메서드
     * @return 모든 공연 정보가 담긴 Response 객체
     */
    @Transactional(readOnly = true)
    public List<GetAllEventResponse> getAllEvents() {

        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(GetAllEventResponse::new)
                .toList();
    }

    /**
     * eventId에 해당하는 공연 정보를 반환하는 메서드
     * @param eventId 조회할 eventId
     * @return 해당 eventId에 해당하는 Event 객체
     */
    @Transactional(readOnly = true)
    public Event getEventFromId(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * userId에 해당하는 유저가 작성한 공연 목록을 조회하는 메서드
     * @param userId 조회할 eventId
     * @return 해당 eventId에 해당하는 Event 객체
     */
    @Transactional(readOnly = true)
    public List<Event> getEventFromUserId(Long userId) {
        return eventRepository.findByUser_Id(userId);
    }

    /**
     * 해당 연 / 월에 진행되는 공연 정보를 조회하는 메서드
     * @param year 조회할 연도
     * @param month 조회할 월
     * @return 해당 연 / 월에 진행되는 공연 정보가 담긴 Response 객체
     */
    @Transactional(readOnly = true)
    public CalenderEventResponse calenderEvents(Integer year, Integer month) {
        LocalDate beginDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, beginDate.lengthOfMonth());

        List<Event> events = eventRepository.findByBetweenTwoDate(beginDate, endDate);

        List<EventSimpleResponse> simpleResponses = events.stream().map(EventSimpleResponse::of).toList();
        return CalenderEventResponse.of(simpleResponses);
    }

    /**
     * Event 객체를 EventDocument 형태로 변환하여 반환하는 메서드
     *      엘라스틱 서치에 데이터를 삽입할 때 사용
     * @param event 변환할 Event 객체
     * @return Event 객체가 변환된 EventDocument 객체
     */
    public EventDocument convertToEventDocument (Event event) {
        // ISO 8601 형식으로 날짜 변환
        String formattedStartDate = event.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String formattedEndDate = event.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);

        return EventDocument.builder()
                .eventId(event.getEventId())
                .category(event.getCategory())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(formattedStartDate)  // 문자열로 변환된 날짜 사용
                .endDate(formattedEndDate)  // 문자열로 변환된 날짜 사용
                .runtime(event.getRuntime())
                .viewRating(event.getViewRating())
                .rating(event.getRating())
                .place(event.getPlace())
                .bookAble(event.getBookAble())
                .imageUrl(event.getImageUrl())
                .build();
    }
}
