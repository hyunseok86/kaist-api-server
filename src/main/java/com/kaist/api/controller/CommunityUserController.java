package com.kaist.api.controller;

import com.kaist.api.entity.CommunityUser;
import com.kaist.api.entity.User;
import com.kaist.api.service.CommunityUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/community-user")
@RequiredArgsConstructor
public class CommunityUserController {

    private final CommunityUserService communityUserService;

    // 커뮤니티에 사용자 가입
    @PostMapping(value = "/join", consumes = "multipart/form-data")
    public ResponseEntity<CommunityUser> joinCommunity(
            @RequestParam("communityId") Long communityId,
            @RequestParam("nickName") String nickName,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication) {
        
        // 현재 인증된 사용자 정보 가져오기
        String userId = authentication.getName();
        
        byte[] imageBytes = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageBytes = image.getBytes();
            } catch (Exception e) {
                throw new RuntimeException("이미지 처리 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
        
        CommunityUser communityUser = communityUserService.createCommunityUser(
                communityId, userId, nickName, type, imageBytes);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(communityUser);
    }

    // 커뮤니티 사용자 정보 수정 (관리자용)
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommunityUser> updateCommunityUser(
            @PathVariable Long id,
            @RequestParam(value = "nickName", required = false) String nickName,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        byte[] imageBytes = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageBytes = image.getBytes();
            } catch (Exception e) {
                throw new RuntimeException("이미지 처리 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
        
        CommunityUser communityUser = communityUserService.updateCommunityUser(
                id, nickName, status, type, imageBytes);
        
        return ResponseEntity.ok(communityUser);
    }

    // 현재 사용자의 커뮤니티 사용자 정보 수정
    @PutMapping(value = "/community/{communityId}/me", consumes = "multipart/form-data")
    public ResponseEntity<CommunityUser> updateMyCommunityUser(
            @PathVariable Long communityId,
            @RequestParam(value = "nickName", required = false) String nickName,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication) {
        
        // 현재 인증된 사용자 정보 가져오기
        String userId = authentication.getName();
        
        byte[] imageBytes = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageBytes = image.getBytes();
            } catch (Exception e) {
                throw new RuntimeException("이미지 처리 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
        
        CommunityUser communityUser = communityUserService.updateMyCommunityUser(
                communityId, userId, nickName, status, type, imageBytes);
        
        return ResponseEntity.ok(communityUser);
    }

    // 커뮤니티 사용자 탈퇴 (관리자용)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCommunityUser(@PathVariable Long id) {
        communityUserService.deleteCommunityUser(id);
        return ResponseEntity.noContent().build();
    }

    // 현재 사용자의 커뮤니티 탈퇴
    @DeleteMapping("/community/{communityId}/me")
    public ResponseEntity<Void> leaveCommunity(
            @PathVariable Long communityId,
            Authentication authentication) {
        
        // 현재 인증된 사용자 정보 가져오기
        String userId = authentication.getName();
        
        // 사용자 ID로 User 엔티티 조회
        Optional<User> userOpt = communityUserService.getUserRepository().findByUserId(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // 해당 커뮤니티에서 사용자의 CommunityUser 조회
        Optional<CommunityUser> communityUserOpt = communityUserService.getCommunityUser(communityId, userOpt.get().getId());
        if (communityUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // CommunityUser 삭제
        communityUserService.deleteCommunityUser(communityUserOpt.get().getId());
        return ResponseEntity.noContent().build();
    }

    // 특정 커뮤니티의 사용자 목록 조회
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<CommunityUser>> getCommunityUsers(@PathVariable Long communityId) {
        List<CommunityUser> users = communityUserService.getCommunityUsers(communityId);
        return ResponseEntity.ok(users);
    }

    // 특정 커뮤니티의 활성 사용자 목록 조회
    @GetMapping("/community/{communityId}/active")
    public ResponseEntity<List<CommunityUser>> getActiveCommunityUsers(@PathVariable Long communityId) {
        List<CommunityUser> users = communityUserService.getActiveCommunityUsers(communityId);
        return ResponseEntity.ok(users);
    }

    // 현재 사용자의 커뮤니티 목록 조회
    @GetMapping("/my-communities")
    public ResponseEntity<List<CommunityUser>> getMyCommunities(Authentication authentication) {
        String userId = authentication.getName();
        
        // 사용자 ID로 User 엔티티 조회
        Optional<User> userOpt = communityUserService.getUserRepository().findByUserId(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<CommunityUser> communities = communityUserService.getUserCommunities(userOpt.get().getId());
        return ResponseEntity.ok(communities);
    }

    // 현재 사용자의 활성 커뮤니티 목록 조회
    @GetMapping("/my-communities/active")
    public ResponseEntity<List<CommunityUser>> getMyActiveCommunities(Authentication authentication) {
        String userId = authentication.getName();
        
        // 사용자 ID로 User 엔티티 조회
        Optional<User> userOpt = communityUserService.getUserRepository().findByUserId(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<CommunityUser> communities = communityUserService.getUserActiveCommunities(userOpt.get().getId());
        return ResponseEntity.ok(communities);
    }

    // 특정 커뮤니티의 특정 사용자 정보 조회
    @GetMapping("/community/{communityId}/user/{userId}")
    public ResponseEntity<CommunityUser> getCommunityUser(
            @PathVariable Long communityId, 
            @PathVariable Long userId) {
        Optional<CommunityUser> communityUser = communityUserService.getCommunityUser(communityId, userId);
        return communityUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 특정 커뮤니티에서 현재 사용자 정보 조회
    @GetMapping("/community/{communityId}/me")
    public ResponseEntity<CommunityUser> getMyCommunityUser(
            @PathVariable Long communityId,
            Authentication authentication) {
        
        // 현재 인증된 사용자 정보 가져오기
        String userId = authentication.getName();
        
        // 사용자 ID로 User 엔티티 조회
        Optional<User> userOpt = communityUserService.getUserRepository().findByUserId(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<CommunityUser> communityUser = communityUserService.getCommunityUser(communityId, userOpt.get().getId());
        return communityUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 커뮤니티 사용자 정렬 순서 변경 (관리자용)
    @PutMapping("/{id}/sort")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommunityUser> updateSortNo(
            @PathVariable Long id,
            @RequestParam("sortNo") Long sortNo) {
        CommunityUser communityUser = communityUserService.updateSortNo(id, sortNo);
        return ResponseEntity.ok(communityUser);
    }

    // 현재 사용자의 커뮤니티 정렬 순서 변경
    @PutMapping("/community/{communityId}/me/sort")
    public ResponseEntity<CommunityUser> updateMySortNo(
            @PathVariable Long communityId,
            @RequestParam("sortNo") Long sortNo,
            Authentication authentication) {
        
        // 현재 인증된 사용자 정보 가져오기
        String userId = authentication.getName();
        
        // 사용자 ID로 User 엔티티 조회
        Optional<User> userOpt = communityUserService.getUserRepository().findByUserId(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // 해당 커뮤니티에서 사용자의 CommunityUser 조회
        Optional<CommunityUser> communityUserOpt = communityUserService.getCommunityUser(communityId, userOpt.get().getId());
        if (communityUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // 정렬 순서 변경
        CommunityUser communityUser = communityUserService.updateSortNo(communityUserOpt.get().getId(), sortNo);
        return ResponseEntity.ok(communityUser);
    }
}
