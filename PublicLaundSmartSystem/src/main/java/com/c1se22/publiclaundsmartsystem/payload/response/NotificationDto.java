package com.c1se22.publiclaundsmartsystem.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
    private Integer id;
    private String message;
    private Integer userId;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
