package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.payload.internal.PushNotificationRequestDto;
import com.c1se22.publiclaundsmartsystem.service.FCMService;
import com.c1se22.publiclaundsmartsystem.service.PushNotificationService;
import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {
    FCMService fcmService;

    @Override
    public void sendPushNotificationToToken(PushNotificationRequestDto pushNotificationRequestDto) throws FirebaseMessagingException{
        fcmService.sendMessageToToken(pushNotificationRequestDto);
        log.info("Push notification sent to token: " + pushNotificationRequestDto.getToken());
    }

    @Override
    public void sendPushNotificationToTopic(PushNotificationRequestDto pushNotificationRequestDto) {
        fcmService.sendMessageWithoutData(pushNotificationRequestDto);
        log.info("Push notification sent to topic: " + pushNotificationRequestDto.getTopic());
    }
}
