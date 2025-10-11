package com.kaist.api.dto;

import com.kaist.api.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String userId;
    private String userName;
    private String status;
    private String email;
    private Role role;
    private Timestamp crateDate;
}
