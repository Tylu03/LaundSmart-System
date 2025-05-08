package com.c1se22.publiclaundsmartsystem.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private Integer userId;
}
