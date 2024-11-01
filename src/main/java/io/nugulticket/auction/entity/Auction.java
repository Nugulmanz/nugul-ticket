package io.nugulticket.auction.entity;

import io.nugulticket.auction.dto.createAction.CreateAuctionRequest;
import io.nugulticket.common.Timestamped;
import io.nugulticket.ticket.entity.Ticket;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    public Auction(CreateAuctionRequest reqDto, Ticket ticket){
        this.startingBid=reqDto.getStartingBid();
        this.startAt=reqDto.getStartAt();
        this.endAt=reqDto.getEndAt();
        this.ticket=ticket;
    }

    public void setBid(int bid){
        this.currentBid=bid;
    }

    public void endAuction(){
        this.isCompleted=true;
    }
}
