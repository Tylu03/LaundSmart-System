package com.c1se22.publiclaundsmartsystem.event.handler;

import com.c1se22.publiclaundsmartsystem.annotation.Loggable;
import com.c1se22.publiclaundsmartsystem.entity.Reservation;
import com.c1se22.publiclaundsmartsystem.enums.ReservationStatus;
import com.c1se22.publiclaundsmartsystem.event.ReservationCreatedEvent;
import com.c1se22.publiclaundsmartsystem.repository.ReservationRepository;
import com.c1se22.publiclaundsmartsystem.service.NotificationService;
import com.c1se22.publiclaundsmartsystem.service.ReservationService;
import com.c1se22.publiclaundsmartsystem.util.AppConstants;
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
public class ReservationEventHandler {
    ReservationService reservationService;
    NotificationService notificationService;
    ReservationRepository reservationRepository;
    TaskScheduler scheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Async
    @EventListener
    @Loggable
    @Transactional(rollbackFor = Exception.class)
    public void handleReservationEvent(ReservationCreatedEvent event) {
        String taskId = "reservation-" + event.getReservation().getId();
        log.info("Processing reservation created event for reservation ID: {}", 
            event.getReservation().getId());
        try {
            Reservation reservation = event.getReservation();
            Runnable task = () -> {
                Reservation currentReservation = reservationRepository.findById(reservation.getId()).orElse(null);
                if (currentReservation != null && currentReservation.getStatus().equals(ReservationStatus.PENDING)){
                    reservationService.cancelReservation(currentReservation.getUser().getUsername());
                    notificationService.sendNotification(currentReservation.getUser().getId(),
                            "Đơn đặt hàng của bạn đã hết hạn và đã bị hủy. Vui lòng thực hiện đặt đơn mới.");
                }
                scheduledTasks.remove(taskId);
            };
            
            ScheduledFuture<?> scheduledFuture = scheduler.schedule(task,
                    Instant.now().plus(AppConstants.TIME_TO_CANCEL_RESERVATION, TimeUnit.MINUTES.toChronoUnit()));
            scheduledTasks.put(taskId, scheduledFuture);
            
            log.info("Successfully scheduled reservation timeout check for reservation ID: {}", 
                event.getReservation().getId());
        } catch (Exception e) {
            log.error("Error processing reservation event: {}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean cancelTask(String taskId) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.get(taskId);
        if (scheduledFuture != null) {
            boolean cancelled = scheduledFuture.cancel(false);
            if (cancelled) {
                scheduledTasks.remove(taskId);
                log.info("Successfully cancelled reservation task with ID: {}", taskId);
            }
            return cancelled;
        }
        return false;
    }
}
