package io.nugulticket.gpt.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.gpt.dto.AskChatRequest;
import io.nugulticket.gpt.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class GptController {

    private final GptService gptService;

    @PostMapping("/ask")
    public ApiResponse<String> askChat(@AuthenticationPrincipal AuthUser authUser, @RequestBody AskChatRequest askChatRequest) {
        Long userId = (authUser != null) ? authUser.getId() : null; // 인증된 사용자 또는 null
        String response = gptService.askChat(userId, askChatRequest.getUserQuestion());
        return ApiResponse.ok(response);
    }
}
