package com.c1se22.publiclaundsmartsystem.payload.internal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FirebaseMachine {
    private String id;
    private String status;
    private Integer duration;
    private String RealtimeStatus;
}
