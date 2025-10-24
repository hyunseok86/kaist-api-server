package com.kaist.api.controller;

import com.kaist.api.dto.CommunityCreateRequest;
import com.kaist.api.dto.CommunityResponse;
import com.kaist.api.dto.CommunityUpdateRequest;
import com.kaist.api.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final CommunityService communityService;

    @PostMapping(value = "/community/create", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommunityResponse> createCommunity(
            @RequestParam("title") String title,
            @RequestParam("summary") String summary,
            @RequestParam("status") String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        CommunityCreateRequest request = CommunityCreateRequest.builder()
                .title(title)
                .summary(summary)
                .status(status)
                .type(type)
                .image(image)
                .build();
        
        CommunityResponse response = communityService.createCommunity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/community/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommunityResponse> updateCommunity(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("summary") String summary,
            @RequestParam("status") String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        CommunityUpdateRequest request = CommunityUpdateRequest.builder()
                .title(title)
                .summary(summary)
                .status(status)
                .type(type)
                .image(image)
                .build();
        
        CommunityResponse response = communityService.updateCommunity(id, request);
        return ResponseEntity.ok(response);
    }
}
