package io.nugulticket.auction.dto.createAction;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateAuctionRequest {
    public int startingBid;
    public LocalDate startAt;
    public LocalDate endAt;
}
