package com.kaist.api.service;

import com.kaist.api.dto.LoginRequest;
import com.kaist.api.dto.LoginResponse;
import com.kaist.api.dto.SignupRequest;
import com.kaist.api.dto.SignupResponse;
import com.kaist.api.dto.UserResponse;
import com.kaist.api.dto.UserSearchRequest;
import com.kaist.api.dto.UserSearchResponse;
import com.kaist.api.entity.Role;
import com.kaist.api.entity.User;
import com.kaist.api.repository.UserRepository;
import com.kaist.api.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostConstruct
    @Transactional
    public void createAdminUserIfNotExists() {
        String adminUserId = "admin";
        if (userRepository.findByUserId(adminUserId).isEmpty()) {
            String encodedPassword = passwordEncoder.encode("123456");
            
            User adminUser = User.builder()
                    .userId(adminUserId)
                    .userName("사이트관리자")
                    .uPassword(encodedPassword)
                    .status("ACTIVE")
                    .email("kaist@gmail.com")
                    .role(Role.ROLE_ADMIN)
                    .build();
            
            userRepository.save(adminUser);
            log.info("Admin user created: {}", adminUserId);
        } else {
            log.info("Admin user already exists: {}", adminUserId);
        }
    }



    
    @Transactional
    public Optional<LoginResponse> authenticate(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUserId(request.getUserId());
        if (!userOpt.isPresent()) {
            return Optional.empty();
        }
        User user = userOpt.get();

        String stored = user.getUPassword();
        String raw = request.getPassword();

        boolean matched;
        if (stored == null) {
            matched = false;
        } else if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            matched = passwordEncoder.matches(raw, stored);
        } else {
            matched = stored.equals(raw);
        }

        if (!matched) {
            return Optional.empty();
        }

        String token = jwtUtil.generateToken(user.getUserId());
        user.setToken(token);
        userRepository.save(user);

        return Optional.of(LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .userName(user.getUserName())
                .message("LOGIN_SUCCESS")
                .build());
    }

    public Optional<SignupResponse> signup(SignupRequest request) {
        if (userRepository.findByUserId(request.getUserId()).isPresent()) {
            return Optional.empty();
        }
        if (request.getUserId() != null && userRepository.findByUserId(request.getUserId()).isPresent()) {
            return Optional.empty();
        }

        String encoded = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .userId(request.getUserId())
                .userName(request.getUserName())
                .uPassword(encoded)
                .status("ACTIVE")
                .email(request.getEmail())
                .role(Role.ROLE_USER)
                .build();

        User saved = userRepository.save(user);
        return Optional.of(SignupResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .userName(saved.getUserName())
                .email(saved.getEmail())
                .message("SIGNUP_SUCCESS")
                .build());
    }

    public Optional<SignupResponse> signupAdmin(SignupRequest request) {
        if (userRepository.findByUserId(request.getUserId()).isPresent()) {
            return Optional.empty();
        }
        if (request.getUserId() != null && userRepository.findByUserId(request.getUserId()).isPresent()) {
            return Optional.empty();
        }

        String encoded = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .userId(request.getUserId())
                .userName(request.getUserName())
                .uPassword(encoded)
                .status("ACTIVE")
                .role(Role.ROLE_ADMIN)
                .email(request.getEmail())
                .build();

        User saved = userRepository.save(user);
        return Optional.of(SignupResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .userName(saved.getUserName())
                .email(saved.getEmail())
                .message("SIGNUP_SUCCESS")
                .build());
    }

    public Optional<UserResponse> getUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .userId(user.getUserId())
                        .userName(user.getUserName())
                        .status(user.getStatus())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .crateDate(user.getCrDt())
                        .build());
    }

    public UserSearchResponse searchUsers(UserSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());


        System.out.println("request.getUserName(): " + request.getUserName());
        
        Page<User> userPage = userRepository.findByUserNameContaining(request.getUserName(), pageable);
        
        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .userId(user.getUserId())
                        .userName(user.getUserName())
                        .status(user.getStatus())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .crateDate(user.getCrDt())
                        .build())
                .collect(Collectors.toList());
        
        return UserSearchResponse.builder()
                .users(userResponses)
                .currentPage(userPage.getNumber())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .size(userPage.getSize())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
    }

    public UserSearchResponse searchUsersByEmail(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByEmailContaining(email, pageable);
        
        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .userId(user.getUserId())
                        .userName(user.getUserName())
                        .status(user.getStatus())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .crateDate(user.getCrDt())
                        .build())
                .collect(Collectors.toList());
        
        return UserSearchResponse.builder()
                .users(userResponses)
                .currentPage(userPage.getNumber())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .size(userPage.getSize())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
    }
}

