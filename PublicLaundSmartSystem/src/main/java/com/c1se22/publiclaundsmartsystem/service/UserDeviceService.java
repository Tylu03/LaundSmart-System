package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.payload.request.UserDeviceRegisterDto;
import com.c1se22.publiclaundsmartsystem.payload.response.UserDeviceResponseDto;

import java.util.List;

public interface UserDeviceService {
    void registerDevice(UserDeviceRegisterDto userDeviceRegisterDto);
    List<String> getActiveUserToken(Integer userId);
    List<UserDeviceResponseDto> getDeviceByUserId(Integer userId);
    boolean isDeviceActive(String fcmToken);
    boolean deactivateDevice(String fcmToken);
    void deleteDevice(String fcmToken);
}
