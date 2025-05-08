package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.event.AppEvent;

public interface EventService {
    void publishEvent(AppEvent event);
}
