package com.kaist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCreateRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다")
    private String title;
    
    @NotBlank(message = "요약은 필수입니다")
    @Size(max = 100, message = "요약은 100자 이하여야 합니다")
    private String summary;
    
    @NotBlank(message = "상태는 필수입니다")
    @Size(max = 10, message = "상태는 10자 이하여야 합니다")
    private String status;
    
    @Size(max = 10, message = "타입은 10자 이하여야 합니다")
    private String type;
}
