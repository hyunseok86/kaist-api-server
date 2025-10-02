package com.kaist.api.service;

import com.kaist.api.dto.LoginRequest;
import com.kaist.api.dto.LoginResponse;
import com.kaist.api.dto.SignupRequest;
import com.kaist.api.dto.SignupResponse;
import com.kaist.api.entity.User;
import com.kaist.api.repository.UserRepository;
import com.kaist.api.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
            return Optional.empty();
        }

        String encoded = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .userId(request.getUserId())
                .userName(request.getUserName())
                .uPassword(encoded)
                .status("ACTIVE")
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
}

