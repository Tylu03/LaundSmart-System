package com.c1se22.publiclaundsmartsystem.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDeviceResponseDto {
    private Integer id;
    private String fcmToken;
    private String deviceType;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
}
