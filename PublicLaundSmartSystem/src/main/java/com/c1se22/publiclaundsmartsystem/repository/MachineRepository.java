package com.c1se22.publiclaundsmartsystem.repository;

import com.c1se22.publiclaundsmartsystem.entity.Machine;
import com.c1se22.publiclaundsmartsystem.payload.MachineInUseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Integer> {
    @Modifying
    @Query(value = "update Machine as m set m.location.id = :locationId where m.id in :machineIds")
    int updateLocationOfMachines(@Param("locationId") Integer locationId, @Param("machineIds") List<Integer> machineIds);
    @Query(value = "select distinct m from Machine m join UsageHistory u on m.id = u.machine.id where m.status = 'IN_USE' and u.user.id = :userId and u.status = 'IN_PROGRESS' group by m.id")
    List<Machine> findMachinesAreBeingUsedByUser(@Param("userId") Integer userId);
    @Query(value = "select m from Machine m join Reservation r on m.id = r.machine.id where r.user.id = :userId and r.status = 'PENDING'")
    Optional<Machine> findMachineAreBeingReservedByUser(@Param("userId") Integer userId);
    @Query(value = "select m from Machine m where m.user.id = :ownerId")
    List<Machine> findMachinesByOwnerId(@Param("ownerId") Integer ownerId);
    @Modifying
    @Query(value = "update Machine as m set m.status = 'DISABLED' where m.id in :machineIds")
    int disableMultipleMachine(@Param("machineIds") List<Integer> machineIds);
    Set<Machine> findByLocationId(Integer locationId);
    boolean existsBySecretId(String secretId);
    Optional<Machine> findBySecretId(String secretId);
}
