package com.c1se22.publiclaundsmartsystem.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserBanStatusDto {
    private boolean isBanned;
    private LocalDateTime banEndDate;
    private Integer banCount;
    private Integer cancellationCount;
}
