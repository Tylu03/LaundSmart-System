package com.c1se22.publiclaundsmartsystem.payload;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineInUseDto {
    private Integer machineId;
    private String machineName;
    private String status;
    private String locationName;
    private String address;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String userName;
    private Integer userId;
}
