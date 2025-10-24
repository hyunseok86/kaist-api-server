package com.kaist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityResponse {
    private Long id;
    private String title;
    private String summary;
    private String status;
    private String type;
    private byte[] image;
    private Timestamp crDt;
    private Timestamp upDt;
}
