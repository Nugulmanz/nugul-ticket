package io.nugulticket.auction.dto.bidAction;

import io.nugulticket.auction.entity.Auction;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BidActionResponse {
    private int startingBid;
    private int currentBid;
    private LocalDate startAt;
    private LocalDate endAt;
    private LocalDate createdAt;

    public BidActionResponse(Auction auction) {
        this.startingBid = auction.getStartingBid();
        this.currentBid = auction.getCurrentBid();
        this.startAt = auction.getStartAt();
        this.endAt = auction.getEndAt();
        this.createdAt = LocalDate.now();
    }
}