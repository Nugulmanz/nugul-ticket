package io.nugulticket.auction.service;

import io.nugulticket.auction.dto.createAction.CreateActionResponse;
import io.nugulticket.auction.dto.createAction.CreateAuctionRequest;
import io.nugulticket.auction.dto.bidAction.BidActionRequest;
import io.nugulticket.auction.dto.bidAction.BidActionResponse;
import io.nugulticket.auction.entity.Auction;
import io.nugulticket.auction.repository.AuctionRepository;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
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
@Transactional(readOnly = true)
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final TicketService ticketService;

    @Transactional
    public CreateActionResponse createAction(CreateAuctionRequest reqDto) {
        Ticket ticket = ticketService.getTicket(reqDto.ticketId);
        Auction auction = new Auction(reqDto, ticket);
        Auction saveAuction = auctionRepository.save(auction);
        return new CreateActionResponse(saveAuction);

    }

    @Transactional
    public BidActionResponse updateAction(long auctionId, BidActionRequest reqDto) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                ()-> new ApiException(ErrorStatus._NOT_FOUND_AUCTION));

        if(auction.getCurrentBid()>=reqDto.getBid()) {
            throw new ApiException(ErrorStatus._Lower_Than_Current_Bid);
        }
        if(LocalDate.now().isAfter(auction.getEndAt())){
            throw new ApiException(ErrorStatus._EXPIRED_ACTION);
        }
        auction.setBid(reqDto.getBid());
        Auction saveAuction = auctionRepository.save(auction);
        return new BidActionResponse(saveAuction);

    }

    public void endAuction(long auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                () ->  new ApiException(ErrorStatus._NOT_FOUND_AUCTION));
        auction.endAuction();
        auctionRepository.save(auction);
    }
}
