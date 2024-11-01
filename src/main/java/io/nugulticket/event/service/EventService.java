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

    // S3
    private final AmazonS3Client s3Client;

    @Value("nugulticket")
    private String bucket;

    public CreateEventResponse createEvent(AuthUser authUser, CreateEventRequest eventRequest, MultipartFile image) {
        int price = 140000;
        int vipSeatCount = 20;
        int rSeatCount = 20;
        int aSeatCount = 20;

        User user = userService.getUser(authUser.getId());

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
                price,
                vipSeatCount,
                rSeatCount,
                aSeatCount);

        return new CreateEventResponse(savedEvent);
    }

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

    @Transactional(readOnly = true)
    public GetEventResponse getEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApiException(ErrorStatus.EVENT_NOT_FOUND));

        return new GetEventResponse(event);
    }

    @Transactional(readOnly = true)
    public List<GetAllEventResponse> getAllEvents() {

        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(GetAllEventResponse::new)
                .toList();
    }

    public Event getEventFromId(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(EntityNotFoundException::new);
    }

    public List<Event> getEventFromUserId(Long userId) {
        return eventRepository.findByUser_Id(userId);
    }

    public CalenderEventResponse calenderEvents(Integer year, Integer month) {
        LocalDate beginDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, beginDate.lengthOfMonth());

        List<Event> events = eventRepository.findByBetweenTwoDate(beginDate, endDate);

        List<EventSimpleResponse> simpleResponses = events.stream().map(EventSimpleResponse::of).toList();
        return CalenderEventResponse.of(simpleResponses);
    }

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
