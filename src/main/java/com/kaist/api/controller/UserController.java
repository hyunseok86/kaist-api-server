package com.kaist.api.controller;

import com.kaist.api.dto.LoginRequest;
import com.kaist.api.dto.LoginResponse;
import com.kaist.api.dto.SignupRequest;
import com.kaist.api.dto.SignupResponse;
import com.kaist.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

   
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody SignupRequest request) {
        Optional<SignupResponse> resp = userService.signup(request);
        if (!resp.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("DUPLICATE_USER");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(resp.get());
    }
}
