package com.kaist.api.service;

import com.kaist.api.dto.CommunityCreateRequest;
import com.kaist.api.dto.CommunityListResponse;
import com.kaist.api.dto.CommunityResponse;
import com.kaist.api.dto.CommunityUpdateRequest;
import com.kaist.api.entity.Community;
import com.kaist.api.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;

    @Transactional
    public CommunityResponse createCommunity(CommunityCreateRequest request) {
        byte[] imageBytes = null;
        
        // 이미지가 있는 경우 바이트 배열로 변환
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                imageBytes = request.getImage().getBytes();
            } catch (Exception e) {
                throw new RuntimeException("이미지 처리 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
        
        Community community = Community.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .status(request.getStatus())
                .type(request.getType())
                .image(imageBytes)
                .build();
        
        Community saved = communityRepository.save(community);
        
        return CommunityResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .summary(saved.getSummary())
                .status(saved.getStatus())
                .type(saved.getType())
                .image(saved.getImage())
                .crDt(saved.getCrDt())
                .upDt(saved.getUpDt())
                .build();
    }

    @Transactional
    public CommunityResponse updateCommunity(Long id, CommunityUpdateRequest request) {
        Optional<Community> communityOpt = communityRepository.findById(id);
        if (communityOpt.isEmpty()) {
            throw new RuntimeException("커뮤니티를 찾을 수 없습니다. ID: " + id);
        }
        
        Community community = communityOpt.get();
        
        // 이미지가 있는 경우에만 업데이트
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                byte[] imageBytes = request.getImage().getBytes();
                community.setImage(imageBytes);
            } catch (Exception e) {
                throw new RuntimeException("이미지 처리 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
        
        // 다른 필드들 업데이트
        community.setTitle(request.getTitle());
        community.setSummary(request.getSummary());
        community.setStatus(request.getStatus());
        if (request.getType() != null) {
            community.setType(request.getType());
        }
        
        Community saved = communityRepository.save(community);
        
        return CommunityResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .summary(saved.getSummary())
                .status(saved.getStatus())
                .type(saved.getType())
                .image(saved.getImage())
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
                        .image(community.getImage())
                        .crDt(community.getCrDt())
                        .upDt(community.getUpDt())
                        .build())
                .collect(Collectors.toList());
        
        return CommunityListResponse.builder()
                .communities(communityResponses)
                .build();
    }
}
