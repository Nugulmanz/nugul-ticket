package io.nugulticket.auction.entity;

import io.nugulticket.auction.dto.createAction.CreateAuctionRequest;
import io.nugulticket.common.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
public class Auction extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;

    private int startingBid;
    private int currentBid=0;
    private boolean isCompleted=false;
    private LocalDate startAt;
    private LocalDate endAt;

    public Auction(CreateAuctionRequest reqDto){
        this.startingBid=reqDto.getStartingBid();
        this.startAt=reqDto.getStartAt();
        this.endAt=reqDto.getEndAt();
    }

    public void setBid(int bid){
        this.currentBid=bid;
    }
}
