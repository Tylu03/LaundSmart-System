package com.c1se22.publiclaundsmartsystem.event.handler;

import com.c1se22.publiclaundsmartsystem.annotation.Loggable;
import com.c1se22.publiclaundsmartsystem.event.WaitMachineEvent;
import com.c1se22.publiclaundsmartsystem.event.WashingCompleteEvent;
import com.c1se22.publiclaundsmartsystem.service.EventService;
import com.c1se22.publiclaundsmartsystem.service.NotificationService;
import com.c1se22.publiclaundsmartsystem.service.UsageHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class WashingCompleteEventHandler {
    TaskScheduler scheduler;
    EventService eventService;
    UsageHistoryService usageHistoryService;
    NotificationService notificationService;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Async
    @EventListener
    @Loggable
    @Transactional(rollbackFor = Exception.class)
    public void handleWashingCompleteEvent(WashingCompleteEvent event) {
        String taskId = "complete-" + event.getUsageHistory().getUsageId();
        log.info("Processing washing complete event for usage ID: {}", 
            event.getUsageHistory().getUsageId());
        try {
            Runnable task = () -> {
                log.info("Executing completion task for usage ID: {}", 
                    event.getUsageHistory().getUsageId());
                usageHistoryService.completeUsageHistory(event.getUsageHistory().getUsageId());
                notificationService.sendNotification(event.getUsageHistory().getUser().getId(),
                        String.format("Máy %s đã giặt xong. Hãy lấy quần áo của bạn.",
                                event.getUsageHistory().getMachine().getName()));
                eventService.publishEvent(new WaitMachineEvent(event.getUsageHistory().getMachine().getId(), 1));
                scheduledTasks.remove(taskId);
            };
            
            ScheduledFuture<?> scheduledFuture = scheduler.schedule(task,
                Instant.now().plus(event.getRemainingTime(), TimeUnit.MINUTES.toChronoUnit()));
            scheduledTasks.put(taskId, scheduledFuture);
            
            log.info("Successfully scheduled complete event for usage ID: {} with task ID: {}", 
                event.getUsageHistory().getUsageId(), taskId);
        } catch (Exception e) {
            log.error("Error processing washing complete event: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void cancelTask(String taskId) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.get(taskId);
        if (scheduledFuture != null) {
            boolean cancelled = scheduledFuture.cancel(false);
            if (cancelled) {
                scheduledTasks.remove(taskId);
                log.info("Successfully cancelled washing complete task with ID: {}", taskId);
            }
        }
    }
}
