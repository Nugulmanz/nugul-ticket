package io.nugulticket.auction.controller;

import io.nugulticket.auction.dto.createAction.CreateActionResponse;
import io.nugulticket.auction.dto.createAction.CreateAuctionRequest;
import io.nugulticket.auction.dto.bidAction.BidActionRequest;
import io.nugulticket.auction.dto.bidAction.BidActionResponse;
import io.nugulticket.auction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auction/v1")
public class AuctionController {

    private final AuctionService auctionService;

    // 경매 생성
    @PostMapping
    public CreateActionResponse createAction(@RequestBody CreateAuctionRequest reqDto) {
        CreateActionResponse resDto = auctionService.createAction(reqDto);
        return resDto;
    }

    // 경매 입찰
    @PutMapping("/{auctionId}/bid")
    public BidActionResponse bidAction(@PathVariable long auctionId, @RequestBody BidActionRequest reqDto) {
        BidActionResponse resDto = auctionService.updateAction(auctionId, reqDto);
        return resDto;
    }

    // 경매 종료 : 이거 필요 없는 것 같은데
    @PutMapping("/{auctionId}")
    public void endAction(@PathVariable long auctionId) {
        auctionService.endAuction(auctionId);

    }



}
