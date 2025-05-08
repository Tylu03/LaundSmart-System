package com.c1se22.publiclaundsmartsystem.payload;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsageReportDto {
    private Integer userId;
    private String userName;
    private String machineName;
    private String locationName;
    private String address;
    private BigDecimal cots;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String typeWash;
}
