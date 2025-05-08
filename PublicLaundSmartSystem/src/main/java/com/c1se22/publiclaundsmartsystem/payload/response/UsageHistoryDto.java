package com.c1se22.publiclaundsmartsystem.payload.response;

import com.c1se22.publiclaundsmartsystem.entity.Machine;
import com.c1se22.publiclaundsmartsystem.entity.User;
import com.c1se22.publiclaundsmartsystem.entity.WashingType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsageHistoryDto {
    private Integer usageId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal cost;
    private Integer machineId;
    private String machineName;
    private Integer userId;
    private String userName;
    private Integer washingTypeId;
    private String washingTypeName;
}
