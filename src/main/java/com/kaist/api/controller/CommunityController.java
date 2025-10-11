package com.kaist.api.controller;

import com.kaist.api.dto.CommunityCreateRequest;
import com.kaist.api.dto.CommunityListResponse;
import com.kaist.api.dto.CommunityResponse;
import com.kaist.api.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CommunityController {
    
    private final CommunityService communityService;

    @PostMapping("/community/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommunityResponse> createCommunity(@Valid @RequestBody CommunityCreateRequest request) {
        CommunityResponse response = communityService.createCommunity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/community/list")
    public ResponseEntity<CommunityListResponse> getCommunityList() {
        CommunityListResponse response = communityService.getCommunityList();
        return ResponseEntity.ok(response);
    }
}
