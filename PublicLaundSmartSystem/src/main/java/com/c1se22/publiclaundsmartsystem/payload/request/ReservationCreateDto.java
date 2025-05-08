package com.c1se22.publiclaundsmartsystem.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationCreateDto {
    @NotNull(message = "Machine id is required")
    private Integer machineId;
    @NotNull(message = "Washing type id is required")
    private Integer washingTypeId;
}
