package com.c1se22.publiclaundsmartsystem.payload.internal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PushNotificationResponseDto {
    private int status;
    private String message;
}
