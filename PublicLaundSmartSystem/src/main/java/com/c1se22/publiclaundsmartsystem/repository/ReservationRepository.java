package com.c1se22.publiclaundsmartsystem.repository;

import com.c1se22.publiclaundsmartsystem.entity.Reservation;
import com.c1se22.publiclaundsmartsystem.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
    List<Reservation> findByUserId(Integer userId);
    List<Reservation> findByMachineId(Integer machineId);
    Reservation findByMachineIdAndStatus(Integer machineId, ReservationStatus status);
    List<Reservation> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r WHERE r.user.id = :userId AND r.status = 'PENDING'")
    boolean findCurrentPendingReservationByUser(@Param("userId") Integer userId);
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.status = 'PENDING'")
    Optional<Reservation> getPendingReservationByUserId(@Param("userId") Integer userId);
}
