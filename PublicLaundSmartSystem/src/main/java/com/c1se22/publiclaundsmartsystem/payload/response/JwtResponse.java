package com.c1se22.publiclaundsmartsystem.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String accessToken;
    private final String tokenType = "Bearer";
    private Integer userId;
}
