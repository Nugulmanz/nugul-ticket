package io.nugulticket.auction.dto.createAuction;

import io.nugulticket.auction.entity.Auction;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateAuctionResponse {
    private Long auctionId;
    private Long ticketId;
    private int startingBid;
    private int currentBid;
    private LocalDate startAt;
    private LocalDate endAt;
    private LocalDate createdAt;

    public CreateAuctionResponse(Auction auction) {
        this.auctionId = auction.getAuctionId();
        this.ticketId = auction.getTicket().getTicketId();
        this.startingBid = auction.getStartingBid();
        this.currentBid = auction.getCurrentBid();
        this.startAt = auction.getStartAt();
        this.endAt = auction.getEndAt();
        this.createdAt = LocalDate.now();
    }
}
