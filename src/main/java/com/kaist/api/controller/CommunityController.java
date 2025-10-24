package com.kaist.api.controller;

import com.kaist.api.dto.CommunityListResponse;
import com.kaist.api.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommunityController {
    
    private final CommunityService communityService;

    @GetMapping("/community/list")
    public ResponseEntity<CommunityListResponse> getCommunityList() {
        CommunityListResponse response = communityService.getCommunityList();
        return ResponseEntity.ok(response);
    }
}
