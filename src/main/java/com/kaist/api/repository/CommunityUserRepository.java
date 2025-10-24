package com.kaist.api.repository;

import com.kaist.api.entity.CommunityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityUserRepository extends JpaRepository<CommunityUser, Long> {
    
    // 커뮤니티 ID로 커뮤니티 사용자 목록 조회
    List<CommunityUser> findByCommunityIdOrderBySortNoAsc(Long communityId);
    
    // 사용자 ID로 커뮤니티 사용자 목록 조회
    List<CommunityUser> findByUserId(Long userId);
    
    // 커뮤니티 ID와 사용자 ID로 특정 커뮤니티 사용자 조회
    Optional<CommunityUser> findByCommunityIdAndUserId(Long communityId, Long userId);
    
    // 커뮤니티 ID로 활성 상태 사용자만 조회
    List<CommunityUser> findByCommunityIdAndStatusOrderBySortNoAsc(Long communityId, String status);
    
    // 사용자 ID와 상태로 커뮤니티 사용자 조회
    List<CommunityUser> findByUserIdAndStatus(Long userId, String status);
    
    // 커뮤니티 ID로 활성 사용자만 조회 (비활성화 제외)
    List<CommunityUser> findByCommunityIdAndStatusNotOrderBySortNoAsc(Long communityId, String status);
    
    // 사용자 ID로 활성 커뮤니티만 조회 (비활성화 제외)
    List<CommunityUser> findByUserIdAndStatusNot(Long userId, String status);
}
