package io.nugulticket.auction.service;

import io.nugulticket.auction.dto.bidAuction.BidAuctionRequest;
import io.nugulticket.auction.dto.bidAuction.BidAuctionResponse;
import io.nugulticket.auction.dto.createAuction.CreateAuctionResponse;
import io.nugulticket.auction.dto.createAuction.CreateAuctionRequest;
import io.nugulticket.auction.entity.Auction;
import io.nugulticket.auction.repository.AuctionRepository;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.lock.RedisDistributedLock;
import io.nugulticket.ticket.entity.Ticket;
import io.nugulticket.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final TicketService ticketService;

    /**
     * 경매를 시작하는 메서드
     *
     * @param reqDto 경매할 티켓과 경매 시간과 관련된 정보가 담긴 Request 객체
     * @return 생성된 경매 정보가 담긴 Response 객체
     */
    @Transactional
    public CreateAuctionResponse createAuction(CreateAuctionRequest reqDto) {
        Ticket ticket = ticketService.getTicket(reqDto.ticketId);
        Auction auction = new Auction(reqDto, ticket);
        Auction saveAuction = auctionRepository.save(auction);
        return new CreateAuctionResponse(saveAuction);

    }

    /**
     * 입찰을 진행하는 메서드
     *
     * @param auctionId 입찰할 경매 ID
     * @param reqDto    가격 정보가 담긴 Request 객체
     * @return 해당 경매의 현재 정보가 담긴 Response 객체
     */
    @RedisDistributedLock(key = "updateAuction")
    public BidAuctionResponse updateAuction(long auctionId, BidAuctionRequest reqDto) {
        Auction auction = auctionRepository.findByIdWithPessimisticLock(auctionId).orElseThrow(
                () -> new ApiException(ErrorStatus._NOT_FOUND_AUCTION));

        if (auction.getCurrentBid() >= reqDto.getBid()) {
            throw new ApiException(ErrorStatus._Lower_Than_Current_Bid);
        }
        if (LocalDate.now().isAfter(auction.getEndAt())) {
            throw new ApiException(ErrorStatus._EXPIRED_ACTION);
        }

        auction.setBid(reqDto.getBid());
        Auction saveAuction = auctionRepository.save(auction);
        return new BidAuctionResponse(saveAuction);
    }

    /**
     * 경매를 종료하는 메서드
     *
     * @param auctionId 종료할 경매 ID
     */
    public void endAuction(long auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                () -> new ApiException(ErrorStatus._NOT_FOUND_AUCTION));
        auction.endAuction();
        auctionRepository.save(auction);
    }
}
