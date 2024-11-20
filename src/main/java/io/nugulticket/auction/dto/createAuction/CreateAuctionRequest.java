package io.nugulticket.auction.dto.createAuction;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateAuctionRequest {
    public Long ticketId;
    public int startingBid;
    public LocalDate startAt;
    public LocalDate endAt;
}
