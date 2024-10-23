package io.nugulticket.auction.dto.createAction;

import io.nugulticket.auction.entity.Auction;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateActionResponse {
    private int startingBid;
    private int currentBid;
    private LocalDate startAt;
    private LocalDate endAt;
    private LocalDate createdAt;

    public CreateActionResponse(Auction auction) {
        this.startingBid = auction.getStartingBid();
        this.currentBid = auction.getCurrentBid();
        this.startAt = auction.getStartAt();
        this.endAt = auction.getEndAt();
        this.createdAt = LocalDate.now();
    }
}
