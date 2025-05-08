package com.c1se22.publiclaundsmartsystem.payload.internal;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.checkerframework.checker.units.qual.N;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDto {
    @NotNull(message = "Reservation id is required")
    private Integer reservationId;
    @NotNull(message = "User id is required")
    private Integer userId;
    @NotNull(message = "Machine id is required")
    private Integer machineId;
    @NotNull(message = "Washing type id is required")
    private Integer washingTypeId;
}
