package com.c1se22.publiclaundsmartsystem.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserUsageDto {
    private String userName;
    private Long usageCount;
    private String userEmail;
}
