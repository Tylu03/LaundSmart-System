package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.event.AppEvent;
import com.c1se22.publiclaundsmartsystem.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    ApplicationEventPublisher publisher;

    @Override
    public void publishEvent(AppEvent event) {
        publisher.publishEvent(event);
    }
}
