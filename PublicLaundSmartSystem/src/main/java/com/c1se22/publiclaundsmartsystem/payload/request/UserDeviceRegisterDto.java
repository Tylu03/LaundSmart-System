package com.c1se22.publiclaundsmartsystem.payload.request;

import com.c1se22.publiclaundsmartsystem.enums.DeviceType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDeviceRegisterDto {
    @NotNull(message = "User id is required")
    private Integer userId;
    @NotNull(message = "Fcm token is required")
    private String fcmToken;
    @NotNull(message = "Device type is required")
    private DeviceType deviceType;
}
