package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.entity.User;
import com.c1se22.publiclaundsmartsystem.entity.UserDevice;
import com.c1se22.publiclaundsmartsystem.exception.ResourceNotFoundException;
import com.c1se22.publiclaundsmartsystem.payload.request.UserDeviceRegisterDto;
import com.c1se22.publiclaundsmartsystem.payload.response.UserDeviceResponseDto;
import com.c1se22.publiclaundsmartsystem.repository.UserDeviceRepository;
import com.c1se22.publiclaundsmartsystem.repository.UserRepository;
import com.c1se22.publiclaundsmartsystem.service.UserDeviceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDeviceServiceImpl implements UserDeviceService {
    UserRepository userRepository;
    UserDeviceRepository userDeviceRepository;
    @Override
    public void registerDevice(UserDeviceRegisterDto userDeviceRegisterDto) {
        User user = userRepository.findById(userDeviceRegisterDto.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userDeviceRegisterDto.getUserId().toString())
        );
        Optional<UserDevice> userDevice = userDeviceRepository.findByFcmToken(userDeviceRegisterDto.getFcmToken());
        if (userDevice.isPresent()){
            UserDevice device = userDevice.get();
            device.setLastLoginAt(LocalDateTime.now());
            userDeviceRepository.save(device);
            return;
        }
        UserDevice device = UserDevice.builder()
                .user(user)
                .deviceType(userDeviceRegisterDto.getDeviceType())
                .fcmToken(userDeviceRegisterDto.getFcmToken())
                .lastLoginAt(LocalDateTime.now())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        userDeviceRepository.save(device);
    }

    @Override
    public List<String> getActiveUserToken(Integer userId) {
        return userDeviceRepository.findByUserIdAndIsActiveTrue(userId).stream().map(UserDevice::getFcmToken).toList();
    }

    @Override
    public List<UserDeviceResponseDto> getDeviceByUserId(Integer userId) {
        List<UserDevice> userDevices = userDeviceRepository.findByUserId(userId);
        if (userDevices != null){
            return userDevices.stream().map((userDevice -> UserDeviceResponseDto.builder()
                   .id(userDevice.getId())
                   .deviceType(userDevice.getDeviceType().name())
                   .fcmToken(userDevice.getFcmToken())
                   .isActive(userDevice.getIsActive())
                   .lastLoginAt(userDevice.getLastLoginAt())
                   .build())).toList();
        }
        return null;
    }

    @Override
    public boolean isDeviceActive(String fcmToken) {
        return userDeviceRepository.existsByFcmTokenAndIsActiveTrue(fcmToken);
    }

    @Override
    public boolean deactivateDevice(String fcmToken) {
        UserDevice userDevice = userDeviceRepository.findByFcmToken(fcmToken).orElseThrow(
                ()-> new ResourceNotFoundException("UserDevice", "fcmToken", fcmToken)
        );
        userDevice.setIsActive(false);
        userDeviceRepository.save(userDevice);
        return true;
    }

    @Override
    @Transactional
    public void deleteDevice(String fcmToken) {
        UserDevice userDevice = userDeviceRepository.findByFcmToken(fcmToken).orElseThrow(
                ()-> new ResourceNotFoundException("UserDevice", "fcmToken", fcmToken)
        );
        userDeviceRepository.delete(userDevice);
    }
}
