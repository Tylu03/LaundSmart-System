package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.payload.internal.PushNotificationRequestDto;
import com.google.firebase.messaging.FirebaseMessagingException;

public interface PushNotificationService {
    void sendPushNotificationToToken(PushNotificationRequestDto pushNotificationRequestDto) throws FirebaseMessagingException;
    void sendPushNotificationToTopic(PushNotificationRequestDto pushNotificationRequestDto);
}
