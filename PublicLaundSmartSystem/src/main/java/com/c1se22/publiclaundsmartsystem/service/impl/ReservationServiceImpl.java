package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.annotation.Loggable;
import com.c1se22.publiclaundsmartsystem.entity.*;
import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import com.c1se22.publiclaundsmartsystem.enums.MachineStatus;
import com.c1se22.publiclaundsmartsystem.enums.ReservationStatus;
import com.c1se22.publiclaundsmartsystem.event.ReservationCreatedEvent;
import com.c1se22.publiclaundsmartsystem.exception.APIException;
import com.c1se22.publiclaundsmartsystem.exception.InsufficientBalanceException;
import com.c1se22.publiclaundsmartsystem.exception.ResourceNotFoundException;
import com.c1se22.publiclaundsmartsystem.payload.request.ReservationCreateDto;
import com.c1se22.publiclaundsmartsystem.payload.internal.ReservationDto;
import com.c1se22.publiclaundsmartsystem.payload.response.ReservationResponseDto;
import com.c1se22.publiclaundsmartsystem.payload.response.UsageHistoryDto;
import com.c1se22.publiclaundsmartsystem.repository.*;
import com.c1se22.publiclaundsmartsystem.service.*;
import com.google.firebase.database.FirebaseDatabase;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {
    ReservationRepository reservationRepository;
    UserRepository userRepository;
    MachineRepository machineRepository;
    WashingTypeRepository washingTypeRepository;
    OwnerWithdrawInfoRepository ownerWithdrawInfoRepository;
    UsageHistoryService usageHistoryService;
    FirebaseDatabase firebaseDatabase;
    EventService eventService;
    UserBanService userBanService;

    @Override
    public List<ReservationResponseDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public ReservationResponseDto getReservationById(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("Reservation", "reservationId", reservationId.toString())
        );
        return mapToResponseDto(reservation);
    }

    @Override
    public List<ReservationResponseDto> getReservationsByUserId(Integer userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        if (reservations != null) {
            return reservations.stream()
                    .map(this::mapToResponseDto)
                    .toList();
        }
        return null;
    }

    @Override
    public List<ReservationResponseDto> getReservationsByMachineId(Integer machineId) {
        List<Reservation> reservations = reservationRepository.findByMachineId(machineId);
        if (reservations != null) {
            return reservations.stream()
                    .map(this::mapToResponseDto)
                    .toList();
        }
        return null;
    }

    @Override
    public List<ReservationResponseDto> getReservationByUsername(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );
        List<Reservation> reservations = reservationRepository.findByUserId(user.getId());
        return reservations.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Loggable
    public ReservationResponseDto createReservation(String username, ReservationCreateDto reservationDto) {
        log.info("Creating reservation for user: {}, machine ID: {}", 
            username, reservationDto.getMachineId());
        try {
            User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(
                    () -> new ResourceNotFoundException("User", "username", username)
            );
            if (reservationRepository.findCurrentPendingReservationByUser(user.getId())) {
                throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.USER_ALREADY_HAS_PENDING_RESERVATION, user.getId());
            }
            Machine machine = machineRepository.findById(reservationDto.getMachineId()).orElseThrow(
                    () -> new ResourceNotFoundException("Machine", "machineId", reservationDto.getMachineId().toString())
            );
            if (!machine.getStatus().name().equals("AVAILABLE")) {
                throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.MACHINE_NOT_AVAILABLE, machine.getId());
            }
            WashingType washingType = washingTypeRepository.findById(reservationDto.getWashingTypeId()).orElseThrow(
                    () -> new ResourceNotFoundException("WashingType", "washingTypeId", reservationDto.getWashingTypeId().toString())
            );

            BigDecimal userBalance = user.getBalance();
            BigDecimal washingTypeCost = washingType.getDefaultPrice();

            if (userBalance.compareTo(washingTypeCost) < 0) {
                throw new InsufficientBalanceException(washingTypeCost, userBalance);
            }

            Reservation reservation = Reservation.builder()
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusMinutes(15))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .status(ReservationStatus.PENDING)
                    .user(user)
                    .machine(machine)
                    .washingType(washingType)
                    .build();
            Machine machine1 = machineRepository.findById(reservationDto.getMachineId()).orElseThrow(
                    () -> new ResourceNotFoundException("Machine", "machineId", reservationDto.getMachineId().toString())
            );
            machine1.setStatus(MachineStatus.RESERVED);
            machineRepository.save(machine1);
            firebaseDatabase.getReference("WashingMachineList")
                    .child(machine1.getSecretId()).child("status").setValueAsync(MachineStatus.RESERVED.name());
            eventService.publishEvent(new ReservationCreatedEvent(reservation));
            log.info("Successfully created reservation with ID: {}", reservation.getId());
            return mapToResponseDto(reservationRepository.save(reservation));
        } catch (Exception e) {
            log.error("Failed to create reservation for user {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ReservationResponseDto updateReservation(Integer reservationId, ReservationDto reservationDto) {
        User user = userRepository.findById(reservationDto.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", reservationDto.getUserId().toString())
        );
        Machine machine = machineRepository.findById(reservationDto.getMachineId()).orElseThrow(
                () -> new ResourceNotFoundException("Machine", "machineId", reservationDto.getMachineId().toString())
        );
        WashingType washingType = washingTypeRepository.findById(reservationDto.getWashingTypeId()).orElseThrow(
                () -> new ResourceNotFoundException("WashingType", "washingTypeId", reservationDto.getWashingTypeId().toString())
        );
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("Reservation", "reservationId", reservationId.toString())
        );
        reservation.setUser(user);
        reservation.setMachine(machine);
        reservation.setWashingType(washingType);
        reservation.setUpdatedAt(LocalDateTime.now());
        return mapToResponseDto(reservationRepository.save(reservation));
    }

    @Override
    @Loggable
    @Transactional(rollbackFor = Exception.class)
    public ReservationResponseDto completeReservation(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );
        Reservation reservation = reservationRepository.getPendingReservationByUserId(user.getId()).orElseThrow(
                () -> new APIException(HttpStatus.NOT_FOUND, ErrorCode.NO_PENDING_RESERVATION, user.getId())
        );
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);

        UsageHistoryDto usageHistoryDTO = UsageHistoryDto.builder()
                .userId(savedReservation.getUser().getId())
                .machineId(savedReservation.getMachine().getId())
                .washingTypeId(savedReservation.getWashingType().getId())
                .cost(savedReservation.getWashingType().getDefaultPrice())
                .build();

        usageHistoryService.createUsageHistory(usageHistoryDTO);
        if (user.getBalance().compareTo(savedReservation.getWashingType().getDefaultPrice()) < 0) {
            throw new InsufficientBalanceException(savedReservation.getWashingType().getDefaultPrice(), user.getBalance());
        }
        user.setBalance(user.getBalance().subtract(savedReservation.getWashingType().getDefaultPrice()));
        userRepository.save(user);
        Machine machine = machineRepository.findById(savedReservation.getMachine().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Machine", "machineId", savedReservation.getMachine().getId().toString())
        );
        machine.setStatus(MachineStatus.IN_USE);
        machineRepository.save(machine);
        firebaseDatabase.getReference("WashingMachineList")
                .child(machine.getSecretId()).child("status").setValueAsync(MachineStatus.IN_USE.name());
        OwnerWithdrawInfo ownerWithdrawInfo = ownerWithdrawInfoRepository.findByOwnerUsername(machine.getUser().getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("OwnerWithdrawInfo", "ownerUsername", machine.getUser().getUsername()));
        ownerWithdrawInfo.setWithdrawAmount(ownerWithdrawInfo.getWithdrawAmount().add(savedReservation.getWashingType().getDefaultPrice()));
        ownerWithdrawInfoRepository.save(ownerWithdrawInfo);
        log.info("Successfully completed reservation with ID: {}", savedReservation.getId());
        return mapToResponseDto(savedReservation);
    }

    @Override
    public ReservationResponseDto getPendingReservationByUserId(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );
        Reservation reservation = reservationRepository.getPendingReservationByUserId(user.getId()).orElseThrow(
                () -> new APIException(HttpStatus.NOT_FOUND, ErrorCode.NO_PENDING_RESERVATION, user.getId())
        );
        return mapToResponseDto(reservation);
    }

    @Override
    public List<ReservationResponseDto> getReservationsForPeriod(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atStartOfDay();
        List<Reservation> reservations = reservationRepository.findByCreatedAtBetween(startTime, endTime);
        return reservations.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public void cancelReservation(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );
        Reservation reservation = reservationRepository.getPendingReservationByUserId(user.getId()).orElseThrow(
                () -> new APIException(HttpStatus.NOT_FOUND, ErrorCode.NO_PENDING_RESERVATION, user.getId())
        );
        reservation.setStatus(ReservationStatus.CANCELED);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now());
        Machine machine = machineRepository.findById(reservation.getMachine().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Machine", "machineId", reservation.getMachine().getId().toString())
        );
        machine.setStatus(MachineStatus.AVAILABLE);
        machineRepository.save(machine);
        reservationRepository.save(reservation);
        userBanService.handleCancelReservation(user.getId());
        log.info("Successfully canceled reservation with ID: {}", reservation.getId());
    }

    @Override
    public void deleteReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("Reservation", "reservationId", reservationId.toString())
        );
        reservation.setStatus(ReservationStatus.ARCHIVED);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        log.info("Successfully deleted reservation with ID: {}", reservation.getId());
    }

    @Override
    public int getTotalReservationsForPeriod(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atStartOfDay();
        return 0;
    }

    @Override
    public int getTotalReservationsForPeriodByMachineId(LocalDateTime start, LocalDateTime end, Integer machineId) {
        return 0;
    }

    @Override
    public int getTotalReservationsForPeriodByUserId(LocalDateTime start, LocalDateTime end, Integer userId) {
        return 0;
    }

    @Override
    public int getTotalReservationsForPeriodByWashingTypeId(LocalDateTime start, LocalDateTime end, Integer washingTypeId) {
        return 0;
    }

    private ReservationResponseDto mapToResponseDto(Reservation reservation) {
        return ReservationResponseDto.builder()
                .reservationId(reservation.getId())
                .status(reservation.getStatus().name())
                .userId(reservation.getUser().getId())
                .machineId(reservation.getMachine().getId())
                .washingType(reservation.getWashingType())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .build();
    }

    private ReservationDto mapToDto(Reservation reservation) {
        return ReservationDto.builder()
                .reservationId(reservation.getId())
                .userId(reservation.getUser().getId())
                .machineId(reservation.getMachine().getId())
                .washingTypeId(reservation.getWashingType().getId())
                .build();
    }
}
