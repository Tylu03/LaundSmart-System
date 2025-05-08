package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.payload.response.UserBanStatusDto;

public interface UserBanService {
    void handleCancelReservation(Integer userId);
    boolean isUserBanned(Integer userId);
    UserBanStatusDto getUserBanStatus(Integer userId);
}
