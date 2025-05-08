package com.c1se22.publiclaundsmartsystem.repository;

import com.c1se22.publiclaundsmartsystem.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserId(Integer userId);
    List<Notification> findByUserIdAndIsRead(Integer userId, Boolean isRead);
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = TRUE WHERE n.user.id = :id")
    void markAllNotificationAsRead(@Param("id") Integer id);
}
