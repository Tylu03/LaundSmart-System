package com.c1se22.publiclaundsmartsystem.event;

import com.c1se22.publiclaundsmartsystem.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationCreatedEvent implements AppEvent {
    private Reservation reservation;
}
