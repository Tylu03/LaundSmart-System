package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.payload.response.NotificationDto;

import java.util.List;

public interface NotificationService {
    NotificationDto getNotificationById(Integer id);
    List<NotificationDto> getNotificationsByUser(String username);
    List<NotificationDto> getUnreadNotificationsByUser(String username);
    List<NotificationDto> getReadNotificationsByUser(String username);
    void markNotificationAsRead(Integer notificationId);
    void markAllNotificationsAsRead(String username);
    void sendNotification(Integer toUserId, String message);
    void sendNotificationToAdminTopic(String message);
    void sendTestNotification();
}
