package com.c1se22.publiclaundsmartsystem.repository;

import com.c1se22.publiclaundsmartsystem.entity.Machine;
import com.c1se22.publiclaundsmartsystem.entity.UsageHistory;
import com.c1se22.publiclaundsmartsystem.enums.UsageHistoryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsageHistoryRepository extends JpaRepository<UsageHistory, Integer> {
    List<UsageHistory> findAllByStartTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    @Query("SELECT u.washingType.id, COUNT(u) FROM UsageHistory u WHERE u.startTime BETWEEN :start AND :end GROUP BY u.machine.id")
    List<Object[]> countByWashingTypeAndStartTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    @Query("SELECT u.washingType.id, SUM(u.cost) FROM UsageHistory u WHERE u.startTime BETWEEN :start AND :end GROUP BY u.machine.id")
    List<Object[]> sumCostByWashingTypeAndStartTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    @Query(value = "SELECT u.user.id, COUNT(u) FROM UsageHistory u WHERE u.startTime BETWEEN :start AND :end GROUP BY u.user.id ORDER BY COUNT(u) DESC")
    List<Object[]> findTopUsersByStartTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);
    @Query("SELECT u.user.id, COUNT(u) FROM UsageHistory u WHERE u.startTime BETWEEN :start AND :end GROUP BY u.user.id")
    List<Object[]> findUserUsageCountByStartTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    @Query("SELECT SUM(u.cost) FROM UsageHistory u WHERE u.startTime BETWEEN :start AND :end")
    BigDecimal sumCostByStartTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    @Query("SELECT COUNT(u) FROM UsageHistory u WHERE u.startTime BETWEEN :start AND :end")
    Long countByStartTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    @Query("SELECT u FROM UsageHistory u WHERE u.machine.id IN :machineIds AND u.user.id = :userId AND u.status = 'IN_PROGRESS'")
    List<UsageHistory> findByCurrentUsedMachineIdsAndUserId(List<Integer> machineIds, Integer userId);
    @Query("SELECT SUM(u.cost) FROM UsageHistory u WHERE u.machine IN :machines")
    BigDecimal sumCostByMachines(@Param("machines") List<Machine> machines);
    @Query("SELECT SUM(u.cost) FROM UsageHistory u WHERE u.machine IN :machines AND u.startTime >= :start AND u.startTime < :end")
    BigDecimal sumCostByMachineInAndStartTimeBetween(
            @Param("machines") List<Machine> machines,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    @Query("SELECT SUM(u.cost) FROM UsageHistory u WHERE u.machine IN :machines AND u.startTime < :date")
    BigDecimal sumCostByMachineInAndStartTimeBefore(
            @Param("machines") List<Machine> machines,
            @Param("date") LocalDateTime date);
    @Query("SELECT COUNT(u) FROM UsageHistory u WHERE u.machine IN :machines AND u.startTime BETWEEN :start AND :end")
    Integer countByMachineInAndStartTimeBetween(
            @Param("machines") List<Machine> machines,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    List<UsageHistory> findAllByUserUsername(String username);
    UsageHistory findByMachineIdAndStatus(Integer machineId, UsageHistoryStatus status);
}
