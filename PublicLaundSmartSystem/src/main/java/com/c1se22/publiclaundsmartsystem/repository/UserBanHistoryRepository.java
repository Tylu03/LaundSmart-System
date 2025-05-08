package com.c1se22.publiclaundsmartsystem.repository;

import com.c1se22.publiclaundsmartsystem.entity.UserBanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBanHistoryRepository extends JpaRepository<UserBanHistory, Integer> {
    UserBanHistory findByUserId(Integer userId);
}
