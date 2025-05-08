package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.annotation.Loggable;
import com.c1se22.publiclaundsmartsystem.entity.*;
import com.c1se22.publiclaundsmartsystem.enums.MachineStatus;
import com.c1se22.publiclaundsmartsystem.enums.UsageHistoryStatus;
import com.c1se22.publiclaundsmartsystem.event.WashingNearCompleteEvent;
import com.c1se22.publiclaundsmartsystem.exception.ResourceNotFoundException;
import com.c1se22.publiclaundsmartsystem.payload.response.UsageHistoryDto;
import com.c1se22.publiclaundsmartsystem.payload.response.UserUsageDto;
import com.c1se22.publiclaundsmartsystem.repository.*;
import com.c1se22.publiclaundsmartsystem.service.EventService;
import com.c1se22.publiclaundsmartsystem.service.MachineService;
import com.c1se22.publiclaundsmartsystem.service.UsageHistoryService;
import com.google.firebase.database.FirebaseDatabase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UsageHistoryServiceImpl implements UsageHistoryService {
    UsageHistoryRepository usageHistoryRepository;
    MachineRepository machineRepository;
    WashingTypeRepository washingTypeRepository;
    OwnerWithdrawInfoRepository ownerWithdrawInfoRepository;
    UserRepository userRepository;

    EventService eventService;
    FirebaseDatabase firebaseDatabase;
    @Override
    public List<UsageHistoryDto> getAllUsageHistories() {
        return usageHistoryRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<UsageHistoryDto> getUsageHistoriesByUsername(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );
        return usageHistoryRepository.findAllByUserUsername(username)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UsageHistoryDto getUsageHistoryById(Integer id) {
        UsageHistory usageHistory = usageHistoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("UsageHistory", "id", id.toString())
        );
        return mapToDto(usageHistory);
    }

    @Override
    @Loggable
    public void createUsageHistory(UsageHistoryDto usageHistoryDto) {
        log.info("Creating usage history for machine ID: {}, user ID: {}", 
            usageHistoryDto.getMachineId(), usageHistoryDto.getUserId());
        try {
            UsageHistory usageHistory = new UsageHistory();
            usageHistory.setCost(usageHistoryDto.getCost());
            usageHistory.setStartTime(LocalDateTime.now());

            Machine machine = machineRepository.findById(usageHistoryDto.getMachineId()).orElseThrow(
                    () -> new ResourceNotFoundException("Machine", "id", usageHistoryDto.getMachineId().toString())
            );
            WashingType washingType = washingTypeRepository.findById(usageHistoryDto.getWashingTypeId()).orElseThrow(
                    () -> new ResourceNotFoundException("WashingType", "id", usageHistoryDto.getWashingTypeId().toString())
            );
            usageHistory.setEndTime(usageHistory.getStartTime().plusMinutes(washingType.getDefaultDuration()));
            User user = userRepository.findById(usageHistoryDto.getUserId()).orElseThrow(
                    () -> new ResourceNotFoundException("User", "id", usageHistoryDto.getUserId().toString())
            );

            usageHistory.setMachine(machine);
            usageHistory.setWashingType(washingType);
            usageHistory.setUser(user);
            usageHistory.setStatus(UsageHistoryStatus.IN_PROGRESS);
            UsageHistory newUsageHistory = usageHistoryRepository.save(usageHistory);

            firebaseDatabase.getReference("WashingMachineList").child(machine.getSecretId()).child("duration")
                    .setValueAsync(washingType.getDefaultDuration());
            eventService.publishEvent(new WashingNearCompleteEvent(newUsageHistory, washingType.getDefaultDuration()));
            log.info("Successfully created usage history with ID: {}", newUsageHistory.getUsageId());
        } catch (Exception e) {
            log.error("Error creating usage history: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void completeUsageHistory(Integer id) {
        UsageHistory usageHistory = usageHistoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("UsageHistory", "id", id.toString())
        );
        usageHistory.setStatus(UsageHistoryStatus.COMPLETED);
        Machine machine = usageHistory.getMachine();
        machine.setStatus(MachineStatus.FINISH);
        machineRepository.save(machine);
        firebaseDatabase.getReference("WashingMachineList").child(machine.getSecretId()).child("duration")
                .setValueAsync(0);
        usageHistoryRepository.save(usageHistory);
    }

    @Override
    public void deleteUsageHistory(Integer id) {
        UsageHistory usageHistory = usageHistoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("UsageHistory", "id", id.toString())
        );
        usageHistoryRepository.delete(usageHistory);
    }

    @Override
    public List<UsageHistoryDto> getUsageHistoriesBetween(LocalDateTime start, LocalDateTime end) {
        return usageHistoryRepository.findAllByStartTimeBetween(start, end).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getUsageCountByWashingType(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = usageHistoryRepository.countByWashingTypeAndStartTimeBetween(start, end);
        Map<String, Long> usageCountByWashingType = new HashMap<>();
        for (Object[] result : results) {
            Integer washingTypeId = (Integer) result[0];
            Long count = (Long) result[1];

            WashingType washingType = washingTypeRepository.findById(washingTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("WashingType", "id", washingTypeId.toString()));

            usageCountByWashingType.put(washingType.getTypeName(), count);
        }
        return usageCountByWashingType;
    }

    @Override
    public Map<String, BigDecimal> getRevenueByWashingType(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = usageHistoryRepository.sumCostByWashingTypeAndStartTimeBetween(start, end);
        Map<String, BigDecimal> revenueByWashingType = new HashMap<>();
        for (Object[] result : results) {
            Integer washingTypeId = (Integer) result[0];
            BigDecimal totalCost = (BigDecimal) result[1];

            WashingType washingType = washingTypeRepository.findById(washingTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("WashingType", "id", washingTypeId.toString()));

            revenueByWashingType.put(washingType.getTypeName(), totalCost);
        }
        return revenueByWashingType;
    }

    @Override
    public List<UserUsageDto> getTopUsers(LocalDateTime start, LocalDateTime end, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = usageHistoryRepository.findTopUsersByStartTimeBetween(start, end, pageable);
        return results.stream().map(result -> {
            Integer userId = (Integer) result[0];
            Long count = (Long) result[1];
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
            return UserUsageDto.builder()
                    .userName(user.getUsername())
                    .usageCount(count)
                    .userEmail(user.getEmail())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getUserUsageCount(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = usageHistoryRepository.findUserUsageCountByStartTimeBetween(start, end);
        Map<String, Long> userUsageCount = new HashMap<>();
        for (Object[] result : results) {
            Integer userId = (Integer) result[0];
            Long count = (Long) result[1];

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

            userUsageCount.put(user.getUsername(), count);
        }
        return userUsageCount;
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        return usageHistoryRepository.sumCostByStartTimeBetween(start, end);
    }

    @Override
    public long getTotalUsageCount(LocalDateTime start, LocalDateTime end) {
        return usageHistoryRepository.countByStartTimeBetween(start, end);
    }

    private UsageHistoryDto mapToDto(UsageHistory usageHistory) {
        return UsageHistoryDto.builder()
                .usageId(usageHistory.getUsageId())
                .startTime(usageHistory.getStartTime())
                .endTime(usageHistory.getEndTime())
                .cost(usageHistory.getCost())
                .machineId(usageHistory.getMachine().getId())
                .machineName(usageHistory.getMachine().getName())
                .userId(usageHistory.getUser().getId())
                .userName(usageHistory.getUser().getUsername())
                .washingTypeId(usageHistory.getWashingType().getId())
                .washingTypeName(usageHistory.getWashingType().getTypeName())
                .build();
    }
}
