package io.nugulticket.user.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.user.dto.UserResponse;
import io.nugulticket.user.dto.UserUpdateRequest;
import io.nugulticket.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PatchMapping("/v1/{userId}")
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal AuthUser authUser,
                                                   @PathVariable Long userId,
                                                   @RequestBody UserUpdateRequest updateRequest) {
        // 현재 로그인한 사용자가 업데이트 계정과 동일한지 확인
        if(!authUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.updateUser(userId, updateRequest));
    }
}
