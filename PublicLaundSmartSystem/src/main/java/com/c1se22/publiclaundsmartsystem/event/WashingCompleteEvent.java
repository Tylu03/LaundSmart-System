package com.c1se22.publiclaundsmartsystem.event;

import com.c1se22.publiclaundsmartsystem.entity.UsageHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WashingCompleteEvent implements AppEvent{
    private UsageHistory usageHistory;
    private Integer remainingTime;
}
