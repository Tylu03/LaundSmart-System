package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import com.c1se22.publiclaundsmartsystem.exception.APIException;
import com.c1se22.publiclaundsmartsystem.payload.internal.PushNotificationRequestDto;
import com.c1se22.publiclaundsmartsystem.service.FCMService;
import com.c1se22.publiclaundsmartsystem.service.UserDeviceService;
import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Slf4j
public class FCMServiceImpl implements FCMService{
    FirebaseMessaging firebaseMessaging;
    UserDeviceService userDeviceService;

    @Override
    public void sendMessage(Map<String, String> data, PushNotificationRequestDto request){
        Message message = getPreconfiguredMessageWithData(data, request);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(message);
        String response = sendAndGetResponse(message);
        log.info("Sent message with data. Topic: " + request.getTopic() + ", " + response+ " msg "+jsonOutput);
    }

    @Override
    public void sendMessageWithoutData(PushNotificationRequestDto request){
        Message message = getPreconfiguredMessageWithoutData(request);
        String response = sendAndGetResponse(message);
        log.info("Sent message without data. Topic: " + request.getTopic() + ", " + response);
    }

    @Override
    public void sendMessageToToken(PushNotificationRequestDto request){
        Message message = getPreconfiguredMessageToToken(request);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(message);
        String response = sendAndGetResponse(message);
        log.info("Sent message to token. Device token: " + request.getToken() + ", " + response+ " msg "+jsonOutput);
    }

    @Override
    public String sendAndGetResponse(Message message) {
        String messageRes;
        try {
            messageRes = firebaseMessaging.sendAsync(message).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error sending message: " + e.getMessage());
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR);
        }
        return messageRes;
    }

    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder().setSound("default")
                        .setColor("#FFFF00").setTag(topic).build()).build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }

    private Message getPreconfiguredMessageToToken(PushNotificationRequestDto request) {
        return getPreconfiguredMessageBuilder(request).setToken(request.getToken())
                .build();
    }

    private Message getPreconfiguredMessageWithoutData(PushNotificationRequestDto request) {
        return getPreconfiguredMessageBuilder(request).setTopic(request.getTopic())
                .build();
    }

    private Message getPreconfiguredMessageWithData(Map<String, String> data, PushNotificationRequestDto request) {
        return getPreconfiguredMessageBuilder(request).putAllData(data).setToken(request.getToken())
                .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequestDto request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        return Message.builder()
                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
                        Notification.builder().setTitle(request.getTitle()).setBody(request.getMessage()).build());
    }
}
