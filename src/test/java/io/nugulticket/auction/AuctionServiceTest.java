package io.nugulticket.auction;

import io.nugulticket.auction.dto.bidAuction.BidAuctionRequest;
import io.nugulticket.auction.dto.bidAuction.BidAuctionResponse;
import io.nugulticket.auction.dto.createAuction.CreateAuctionRequest;
import io.nugulticket.auction.dto.createAuction.CreateAuctionResponse;
import io.nugulticket.auction.entity.Auction;
import io.nugulticket.auction.repository.AuctionRepository;
import io.nugulticket.auction.service.AuctionService;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @InjectMocks
    private AuctionService auctionService;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private TicketService ticketService;

    private Ticket mockTicket;
    private Auction mockAuction;

    @BeforeEach
    void setUp() {
        mockTicket = new Ticket(); // Ticket의 기본 생성자나 테스트에 필요한 속성으로 설정해줘야 함
        mockAuction = new Auction(); // Auction의 기본 생성자나 테스트에 필요한 속성으로 설정해줘야 함
    }

    @Test
    void testCreateAuction_Success() {
        // Given
        CreateAuctionRequest request = new CreateAuctionRequest();
        ReflectionTestUtils.setField(request, "ticketId", 1L);

        Auction auction = new Auction(request, mockTicket);


        when(ticketService.getTicket(1L)).thenReturn(mockTicket);

        // Auction 객체를 생성할 때, CreateAuctionRequest와 mockTicket을 직접 주입
        when(auctionRepository.save(any())).thenReturn(auction);

        // When
        CreateAuctionResponse response = auctionService.createAuction(request);

        // Then
        assertNotNull(response);
        verify(ticketService, times(1)).getTicket(1L);
        verify(auctionRepository, times(1)).save(any(Auction.class));
    }
    //여기서 when then만 조금식 바꾸면 됨

    @Test
    void testUpdateAuction_Success() {
        // Given
        long auctionId = 1L;
        BidAuctionRequest request = new BidAuctionRequest();
        ReflectionTestUtils.setField(request, "bid", 200);

        // Auction 객체의 필드 값을 직접 주입
        mockAuction = new Auction(); // 필요한 경우에 맞는 생성자 사용
        ReflectionTestUtils.setField(mockAuction, "currentBid", 100); // 현재 입찰가 설정
        ReflectionTestUtils.setField(mockAuction, "endAt", LocalDate.now().plusDays(1)); // 경매 종료 날짜 설정

        when(auctionRepository.findByIdWithPessimisticLock(auctionId)).thenReturn(Optional.of(mockAuction));
        when(auctionRepository.save(any(Auction.class))).thenReturn(mockAuction);

        // When
        BidAuctionResponse response = auctionService.updateAuction(auctionId, request);

        // Then
        assertNotNull(response);
        verify(auctionRepository, times(1)).findByIdWithPessimisticLock(auctionId);
        verify(auctionRepository, times(1)).save(any(Auction.class));
    }

    @Test
    void testUpdateAuction_Fails_LowerBid() {
        // Given
        long auctionId = 1L;
        BidAuctionRequest request = new BidAuctionRequest();
        ReflectionTestUtils.setField(request, "bid", 50); // 현재 입찰가보다 낮은 값

        mockAuction = new Auction();
        ReflectionTestUtils.setField(mockAuction, "currentBid", 100); // 현재 입찰가 설정
        when(auctionRepository.findByIdWithPessimisticLock(auctionId)).thenReturn(Optional.of(mockAuction));

        // When & Then: 예외가 발생하는지만 확인
        assertThrows(ApiException.class, () -> auctionService.updateAuction(auctionId, request));

        // Repository 호출 검증
        verify(auctionRepository, times(1)).findByIdWithPessimisticLock(auctionId);
        verify(auctionRepository, never()).save(any(Auction.class));
    }

    @Test
    void testUpdateAuction_Fails_ExpiredAuction() {
        // Given
        long auctionId = 1L;
        BidAuctionRequest request = new BidAuctionRequest();
        ReflectionTestUtils.setField(request, "bid", 200);

        mockAuction = new Auction();
        ReflectionTestUtils.setField(mockAuction, "currentBid", 100);
        ReflectionTestUtils.setField(mockAuction, "endAt", LocalDate.now().minusDays(1)); // 이미 마감된 경매

        when(auctionRepository.findByIdWithPessimisticLock(auctionId)).thenReturn(Optional.of(mockAuction));

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () ->
                auctionService.updateAuction(auctionId, request));

        verify(auctionRepository, atLeastOnce()).findByIdWithPessimisticLock(auctionId);
        verify(auctionRepository, never()).save(any(Auction.class));
    }

    @Test
    void testUpdateAuction_Fails_NotFound() {
        // Given
        long auctionId = 1L;
        BidAuctionRequest request = new BidAuctionRequest();
        ReflectionTestUtils.setField(request, "bid", 200);

        mockAuction = new Auction();
        ReflectionTestUtils.setField(mockAuction, "currentBid", 100);
        ReflectionTestUtils.setField(mockAuction, "endAt", LocalDate.now().minusDays(1)); // 이미 마감된 경매

        when(auctionRepository.findByIdWithPessimisticLock(auctionId)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () ->
                auctionService.updateAuction(auctionId, request));


        assertEquals(request.getBid(), 200);
        assertEquals(exception.getErrorCode(), ErrorStatus._NOT_FOUND_AUCTION);
        verify(auctionRepository, atLeastOnce()).findByIdWithPessimisticLock(auctionId);
        verify(auctionRepository, never()).save(any(Auction.class));
    }

    @Test
    void testEndAuction_Error_NotFound() {
        // Given
        long auctionId = 1L;

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () ->
                auctionService.endAuction(auctionId));

        assertEquals(exception.getErrorCode(), ErrorStatus._NOT_FOUND_AUCTION);
        verify(auctionRepository, never()).save(any(Auction.class));
    }

    @Test
    void testEndAuction() {
        // Given
        long auctionId = 1L;

        mockAuction = new Auction();
        ReflectionTestUtils.setField(mockAuction, "currentBid", 100);
        ReflectionTestUtils.setField(mockAuction, "endAt", LocalDate.now().minusDays(1)); // 이미 마감된 경매

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(mockAuction));

        // When & Then
        auctionService.endAuction(auctionId);

        verify(auctionRepository, atLeastOnce()).save(any(Auction.class));
    }

}
