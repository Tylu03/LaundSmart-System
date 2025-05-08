package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.payload.request.ReservationCreateDto;
import com.c1se22.publiclaundsmartsystem.payload.internal.ReservationDto;
import com.c1se22.publiclaundsmartsystem.payload.response.ReservationResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {
    List<ReservationResponseDto> getAllReservations();
    ReservationResponseDto getReservationById(Integer reservationId);
    List<ReservationResponseDto> getReservationsByUserId(Integer userId);
    List<ReservationResponseDto> getReservationsByMachineId(Integer machineId);
    List<ReservationResponseDto> getReservationByUsername(String username);
    ReservationResponseDto createReservation(String username, ReservationCreateDto reservationDto);
    ReservationResponseDto updateReservation(Integer reservationId, ReservationDto reservationDto);
    ReservationResponseDto completeReservation(String username);
    ReservationResponseDto getPendingReservationByUserId(String username);
    List<ReservationResponseDto> getReservationsForPeriod(LocalDate start, LocalDate end);
    void cancelReservation(String username);
    void deleteReservation(Integer reservationId);
    int getTotalReservationsForPeriod(LocalDate start, LocalDate end);
    int getTotalReservationsForPeriodByMachineId(LocalDateTime start, LocalDateTime end, Integer machineId);
    int getTotalReservationsForPeriodByUserId(LocalDateTime start, LocalDateTime end, Integer userId);
    int getTotalReservationsForPeriodByWashingTypeId(LocalDateTime start, LocalDateTime end, Integer washingTypeId);
}
