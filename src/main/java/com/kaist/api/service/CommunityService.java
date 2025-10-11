package com.kaist.api.service;

import com.kaist.api.dto.CommunityCreateRequest;
import com.kaist.api.dto.CommunityListResponse;
import com.kaist.api.dto.CommunityResponse;
import com.kaist.api.entity.Community;
import com.kaist.api.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;

    @Transactional
    public CommunityResponse createCommunity(CommunityCreateRequest request) {
        Community community = Community.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .status(request.getStatus())
                .type(request.getType())
                .build();
        
        Community saved = communityRepository.save(community);
        
        return CommunityResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .summary(saved.getSummary())
                .status(saved.getStatus())
                .type(saved.getType())
                .crDt(saved.getCrDt())
                .upDt(saved.getUpDt())
                .build();
    }

    public CommunityListResponse getCommunityList() {
        List<Community> communities = communityRepository.findAll();
        
        // 생성일 기준 내림차순 정렬
        List<CommunityResponse> communityResponses = communities.stream()
                .sorted((c1, c2) -> c2.getCrDt().compareTo(c1.getCrDt()))
                .map(community -> CommunityResponse.builder()
                        .id(community.getId())
                        .title(community.getTitle())
                        .summary(community.getSummary())
                        .status(community.getStatus())
                        .type(community.getType())
                        .crDt(community.getCrDt())
                        .upDt(community.getUpDt())
                        .build())
                .collect(Collectors.toList());
        
        return CommunityListResponse.builder()
                .communities(communityResponses)
                .build();
    }
}
