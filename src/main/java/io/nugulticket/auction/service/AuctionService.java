package io.nugulticket.auction.service;

import io.nugulticket.auction.dto.createAction.CreateActionResponse;
import io.nugulticket.auction.dto.createAction.CreateAuctionRequest;
import io.nugulticket.auction.dto.bidAction.BidActionRequest;
import io.nugulticket.auction.dto.bidAction.BidActionResponse;
import io.nugulticket.auction.entity.Auction;
import io.nugulticket.auction.repository.AuctionRepository;
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

    @Transactional
    public CreateActionResponse createAction(CreateAuctionRequest reqDto) {
        Auction auction = new Auction(reqDto);
        Auction saveAuction = auctionRepository.save(auction);
        return new CreateActionResponse(saveAuction);

    }

    @Transactional
    public BidActionResponse updateAction(long auctionId, BidActionRequest reqDto) {
        Auction auction = auctionRepository.findById(auctionId).orElse(null);
        if(auction.getCurrentBid()>=reqDto.getBid()) {
            // 현재 입찰가 보다 낮은 금액으로 입찰할 수 없습니다.
            log.info("입찰가 에러 발생한 거야");
        }
        if(LocalDate.now().isAfter(auction.getEndAt())){
            // 종료된 경매입니다.
            log.info("종료된 경매 에러 발생한 거야");
            System.out.println(LocalDate.now());
        }
        auction.setBid(reqDto.getBid());
        Auction saveAuction = auctionRepository.save(auction);
        return new BidActionResponse(saveAuction);

    }
}
