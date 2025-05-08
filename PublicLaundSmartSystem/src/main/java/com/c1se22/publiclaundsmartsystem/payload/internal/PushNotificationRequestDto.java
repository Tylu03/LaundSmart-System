package com.c1se22.publiclaundsmartsystem.payload.internal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PushNotificationRequestDto {
    private String title;
    private String message;
    private String topic;
    private String token;
}
