package com.kaist.api.controller;

import com.kaist.api.dto.ErrorResponse;
import com.kaist.api.dto.UserResponse;
import com.kaist.api.dto.UserSearchRequest;
import com.kaist.api.dto.UserSearchResponse;
import com.kaist.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        //관리자가 사용자를 조회 할 수 있어야 함
        //자신의 아이디의 정보를 가져올 수 있어야 함
        // 현재 인증된 사용자가 관리자 권한을 가지고 있는지 확인하는 로직
        // Spring Security의 Authentication을 활용
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = false;
        if (authentication != null && authentication.isAuthenticated()) {
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }

        // 인증된 사용자와 파라미터로 받은 사용자가 일치하는지 체크
        // 인증 객체에서 principal(로그인된 userId)와 요청 userId가 같은지 확인
        String authenticatedUserId = authentication != null ? authentication.getName() : null;

        if (isAdmin || authenticatedUserId != null && authenticatedUserId.equals(userId)) {
            Optional<UserResponse> user = userService.getUserByUserId(userId);
            if (!user.isPresent()) {
                ErrorResponse error = ErrorResponse.builder()
                        .message("사용자를 찾을 수 없습니다")
                        .error("USER_NOT_FOUND")
                        .status(404)
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok(user.get());
        }else{
            ErrorResponse error = ErrorResponse.builder()
                    .message("접근 권한이 없습니다")
                    .error("ACCESS_DENIED")
                    .status(403)
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserSearchResponse> searchUsers(
            @RequestParam String userName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        UserSearchRequest request = UserSearchRequest.builder()
                .userName(userName)
                .page(page)
                .size(size)
                .build();
        
        UserSearchResponse response = userService.searchUsers(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/email")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserSearchResponse> searchUsersByEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        UserSearchResponse response = userService.searchUsersByEmail(email, page, size);
        return ResponseEntity.ok(response);
    }
}
