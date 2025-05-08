package com.c1se22.publiclaundsmartsystem.repository;

import com.c1se22.publiclaundsmartsystem.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Integer> {
    List<UserDevice> findByUserIdAndIsActiveTrue(Integer userId);
    List<UserDevice> findByUserId(Integer userId);
    Optional<UserDevice> findByFcmToken(String fcmToken);
    boolean existsByFcmTokenAndIsActiveTrue(String fcmToken);
}
