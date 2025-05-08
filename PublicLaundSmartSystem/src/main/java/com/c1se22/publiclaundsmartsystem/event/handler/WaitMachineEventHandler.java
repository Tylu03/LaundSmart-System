package com.c1se22.publiclaundsmartsystem.event.handler;

import com.c1se22.publiclaundsmartsystem.annotation.Loggable;
import com.c1se22.publiclaundsmartsystem.enums.MachineStatus;
import com.c1se22.publiclaundsmartsystem.event.WaitMachineEvent;
import com.c1se22.publiclaundsmartsystem.service.MachineService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class WaitMachineEventHandler {
    MachineService machineService;
    TaskScheduler scheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Async
    @EventListener
    @Loggable
    @Transactional(rollbackFor = Exception.class)
    public void handleWaitMachineEvent(WaitMachineEvent event) {
        String taskId = "wait-" + event.getMachineId();
        log.info("Processing wait machine event for machine ID: {}", event.getMachineId());
        try {
            Runnable task = () -> {
                log.info("Executing machine waiting task for machine ID: {}",
                        event.getMachineId());
                machineService.updateMachineStatus(event.getMachineId(), MachineStatus.AVAILABLE.name());
                scheduledTasks.remove(taskId);
            };
            
            ScheduledFuture<?> scheduledFuture = scheduler.schedule(task,
                    Instant.now().plus(event.getDuration(), TimeUnit.MINUTES.toChronoUnit()));
            scheduledTasks.put(taskId, scheduledFuture);
            
            log.info("Successfully scheduled machine waiting event for machine ID: {} with task ID: {}",
                    event.getMachineId(), taskId);
        } catch (Exception e) {
            log.error("Error processing machine waiting event: {}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean cancelTask(String taskId) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.get(taskId);
        if (scheduledFuture != null) {
            boolean cancelled = scheduledFuture.cancel(false);
            if (cancelled) {
                scheduledTasks.remove(taskId);
                log.info("Successfully cancelled wait machine task with ID: {}", taskId);
            }
            return cancelled;
        }
        return false;
    }
}
