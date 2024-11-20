package io.nugulticket.auction.controller;

import io.nugulticket.auction.dto.bidAuction.BidAuctionRequest;
import io.nugulticket.auction.dto.bidAuction.BidAuctionResponse;
import io.nugulticket.auction.dto.createAuction.CreateAuctionResponse;
import io.nugulticket.auction.dto.createAuction.CreateAuctionRequest;
import io.nugulticket.auction.service.AuctionService;
import io.nugulticket.common.apipayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auction/v1")
public class AuctionController {

    private final AuctionService auctionService;

    // 경매 생성
    @PostMapping
    public ApiResponse<CreateAuctionResponse> createAuction(@RequestBody CreateAuctionRequest reqDto) {
        CreateAuctionResponse resDto = auctionService.createAuction(reqDto);
        return ApiResponse.ok(resDto);
    }

    // 경매 입찰
    @PutMapping("/{auctionId}/bid")
    public ApiResponse<BidAuctionResponse> bidAuction(@PathVariable long auctionId, @RequestBody BidAuctionRequest reqDto) {
        BidAuctionResponse resDto = auctionService.updateAuction(auctionId, reqDto);
        return ApiResponse.ok(resDto);
    }

    // 경매 종료 : 이거 필요 없는 것 같은데
    @PutMapping("/{auctionId}")
    public void endAuction(@PathVariable long auctionId) {
        auctionService.endAuction(auctionId);

    }


}
