package com.kaist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    @NotBlank(message = "사용자 ID는 필수입니다")
    @Size(max = 30)
    private String userId;

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 15)
    private String userName;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, max = 100)
    private String password;

    @Email
    @Size(max = 30)
    private String email;
}
