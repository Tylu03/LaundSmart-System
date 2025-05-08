package com.c1se22.publiclaundsmartsystem.repository;

import com.c1se22.publiclaundsmartsystem.entity.OwnerWithdrawInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerWithdrawInfoRepository extends JpaRepository<OwnerWithdrawInfo, Integer> {
    Optional<OwnerWithdrawInfo> findByOwnerUsername(String ownerUsername);
}
