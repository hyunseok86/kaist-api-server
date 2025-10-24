package com.kaist.api.service;

import com.kaist.api.entity.CommunityUser;
import com.kaist.api.entity.User;
import com.kaist.api.repository.CommunityUserRepository;
import com.kaist.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityUserService {

    private final CommunityUserRepository communityUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityUser createCommunityUser(Long communityId, String userId, String nickName, String type, byte[] image) {
        

        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId);
        }

        //이미 해당 커뮤니티에 가입된 사용자인지 확인
        Optional<CommunityUser> existingUser = communityUserRepository.findByCommunityIdAndUserId(communityId, user.get().getId());
        if (existingUser.isPresent()) {
            throw new RuntimeException("이미 해당 커뮤니티에 가입된 사용자입니다.");
        }


        // 다음 sortNo 계산
        List<CommunityUser> existingUsers = communityUserRepository.findByCommunityIdOrderBySortNoAsc(communityId);
        Long nextSortNo = existingUsers.isEmpty() ? 1L : existingUsers.get(existingUsers.size() - 1).getSortNo() + 1;

        CommunityUser communityUser = CommunityUser.builder()
                .communityId(communityId)
                .userId(user.get().getId())
                .sortNo(nextSortNo)
                .nickName(nickName)
                .status("C") // 기본값: C (활성)
                .type(type)
                .image(image)
                .build();

        return communityUserRepository.save(communityUser);
    }

    @Transactional
    public CommunityUser updateCommunityUser(Long id, String nickName, String status, String type, byte[] image) {
        Optional<CommunityUser> communityUserOpt = communityUserRepository.findById(id);
        if (communityUserOpt.isEmpty()) {
            throw new RuntimeException("커뮤니티 사용자를 찾을 수 없습니다. ID: " + id);
        }

        CommunityUser communityUser = communityUserOpt.get();
        
        if (nickName != null) {
            communityUser.setNickName(nickName);
        }
        if (status != null) {
            communityUser.setStatus(status);
        }
        if (type != null) {
            communityUser.setType(type);
        }
        if (image != null) {
            communityUser.setImage(image);
        }

        return communityUserRepository.save(communityUser);
    }

    @Transactional
    public CommunityUser updateMyCommunityUser(Long communityId, String userId, String nickName, String status, String type, byte[] image) {
        // 사용자 ID로 User 엔티티 조회
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId);
        }

        // 해당 커뮤니티에서 사용자의 CommunityUser 조회
        Optional<CommunityUser> communityUserOpt = communityUserRepository.findByCommunityIdAndUserId(communityId, userOpt.get().getId());
        if (communityUserOpt.isEmpty()) {
            throw new RuntimeException("해당 커뮤니티에 가입되지 않은 사용자입니다.");
        }

        CommunityUser communityUser = communityUserOpt.get();
        
        if (nickName != null) {
            communityUser.setNickName(nickName);
        }
        if (status != null) {
            communityUser.setStatus(status);
        }
        if (type != null) {
            communityUser.setType(type);
        }
        if (image != null) {
            communityUser.setImage(image);
        }

        return communityUserRepository.save(communityUser);
    }

    @Transactional
    public void deleteCommunityUser(Long id) {
        Optional<CommunityUser> communityUserOpt = communityUserRepository.findById(id);
        if (communityUserOpt.isEmpty()) {
            throw new RuntimeException("커뮤니티 사용자를 찾을 수 없습니다. ID: " + id);
        }

        communityUserRepository.deleteById(id);
    }

    @Transactional
    public CommunityUser deactivateCommunityUser(Long id) {
        Optional<CommunityUser> communityUserOpt = communityUserRepository.findById(id);
        if (communityUserOpt.isEmpty()) {
            throw new RuntimeException("커뮤니티 사용자를 찾을 수 없습니다. ID: " + id);
        }

        CommunityUser communityUser = communityUserOpt.get();
        communityUser.setStatus("D"); // D = 비활성화 (Deactivated)
        
        return communityUserRepository.save(communityUser);
    }

    @Transactional
    public CommunityUser deactivateMyCommunityUser(Long communityId, String userId) {
        // 사용자 ID로 User 엔티티 조회
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId);
        }

        // 해당 커뮤니티에서 사용자의 CommunityUser 조회
        Optional<CommunityUser> communityUserOpt = communityUserRepository.findByCommunityIdAndUserId(communityId, userOpt.get().getId());
        if (communityUserOpt.isEmpty()) {
            throw new RuntimeException("해당 커뮤니티에 가입되지 않은 사용자입니다.");
        }

        CommunityUser communityUser = communityUserOpt.get();
        communityUser.setStatus("D"); // D = 비활성화 (Deactivated)
        
        return communityUserRepository.save(communityUser);
    }

    public List<CommunityUser> getCommunityUsers(Long communityId) {
        return communityUserRepository.findByCommunityIdOrderBySortNoAsc(communityId);
    }

    public List<CommunityUser> getActiveCommunityUsers(Long communityId) {
        return communityUserRepository.findByCommunityIdAndStatusOrderBySortNoAsc(communityId, "C");
    }

    // 비활성화된 사용자를 제외한 커뮤니티 사용자 목록
    public List<CommunityUser> getActiveCommunityUsersExcludingDeactivated(Long communityId) {
        return communityUserRepository.findByCommunityIdAndStatusNotOrderBySortNoAsc(communityId, "D");
    }

    public List<CommunityUser> getUserCommunities(Long userId) {
        return communityUserRepository.findByUserId(userId);
    }

    public List<CommunityUser> getUserActiveCommunities(Long userId) {
        return communityUserRepository.findByUserIdAndStatus(userId, "C");
    }

    // 비활성화된 커뮤니티를 제외한 사용자 커뮤니티 목록
    public List<CommunityUser> getUserActiveCommunitiesExcludingDeactivated(Long userId) {
        return communityUserRepository.findByUserIdAndStatusNot(userId, "D");
    }

    public Optional<CommunityUser> getCommunityUser(Long communityId, Long userId) {
        return communityUserRepository.findByCommunityIdAndUserId(communityId, userId);
    }

    @Transactional
    public CommunityUser updateSortNo(Long id, Long newSortNo) {
        Optional<CommunityUser> communityUserOpt = communityUserRepository.findById(id);
        if (communityUserOpt.isEmpty()) {
            throw new RuntimeException("커뮤니티 사용자를 찾을 수 없습니다. ID: " + id);
        }

        CommunityUser communityUser = communityUserOpt.get();
        communityUser.setSortNo(newSortNo);
        
        return communityUserRepository.save(communityUser);
    }

    // UserRepository 접근을 위한 메서드
    public UserRepository getUserRepository() {
        return userRepository;
    }
}
