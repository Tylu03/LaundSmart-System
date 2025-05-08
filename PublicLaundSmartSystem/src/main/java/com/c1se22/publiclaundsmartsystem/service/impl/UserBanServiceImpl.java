package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.entity.User;
import com.c1se22.publiclaundsmartsystem.entity.UserBanHistory;
import com.c1se22.publiclaundsmartsystem.exception.ResourceNotFoundException;
import com.c1se22.publiclaundsmartsystem.payload.response.UserBanStatusDto;
import com.c1se22.publiclaundsmartsystem.repository.UserBanHistoryRepository;
import com.c1se22.publiclaundsmartsystem.repository.UserRepository;
import com.c1se22.publiclaundsmartsystem.service.UserBanService;
import com.c1se22.publiclaundsmartsystem.util.AppConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@AllArgsConstructor
public class UserBanServiceImpl implements UserBanService {
    UserBanHistoryRepository userBanHistoryRepository;
    UserRepository userRepository;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleCancelReservation(Integer userId) {
        UserBanHistory userBanHistory = userBanHistoryRepository.findByUserId(userId);
        if (userBanHistory == null){
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new ResourceNotFoundException("User", "id", userId.toString()));
            userBanHistory = new UserBanHistory();
            userBanHistory.setUser(user);
        }
        if (isNewMonth(userBanHistory)){
            userBanHistory.setCancellationCount(0);
        }
        userBanHistory.setCancellationCount(userBanHistory.getCancellationCount() + 1);
        if (userBanHistory.getCancellationCount() >= AppConstants.MAX_MONTHLY_CANCEL_RESERVATION){
            banUser(userId);
        }
        userBanHistoryRepository.save(userBanHistory);
    }

    @Override
    public boolean isUserBanned(Integer userId) {
        UserBanHistory userBanHistory = userBanHistoryRepository.findByUserId(userId);
        if (userBanHistory == null){
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (userBanHistory.getLastBanStart() == null){
            return false;
        }
        return now.isBefore(userBanHistory.getLastBanEnd());
    }

    @Override
    public UserBanStatusDto getUserBanStatus(Integer userId) {
        UserBanHistory userBanHistory = userBanHistoryRepository.findByUserId(userId);
        if (userBanHistory == null){
            return UserBanStatusDto.builder()
                    .banCount(0)
                    .banEndDate(null)
                    .cancellationCount(0)
                    .isBanned(false)
                    .build();
        }
        return UserBanStatusDto.builder()
                .banCount(userBanHistory.getBanCount())
                .banEndDate(userBanHistory.getLastBanEnd())
                .cancellationCount(userBanHistory.getCancellationCount())
                .isBanned(isUserBanned(userId))
                .build();
    }

    private void banUser(Integer userId) {
        UserBanHistory userBanHistory = userBanHistoryRepository.findByUserId(userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userId.toString()));
        if (userBanHistory == null){
            userBanHistory = new UserBanHistory();
            userBanHistory.setUser(user);
        }
        if (userBanHistory.getBanCount() >= AppConstants.MAX_BAN_BEFORE_DELETE){
            user.setIsActive(false);
            userRepository.save(user);
            return;
        }
        userBanHistory.setBanCount(userBanHistory.getBanCount() + 1);
        userBanHistory.setCancellationCount(0);
        userBanHistory.setLastBanStart(LocalDateTime.now());
        userBanHistory.setLastBanEnd(LocalDateTime.now().plusDays(
                AppConstants.BAN_DURATION[Math.min(userBanHistory.getBanCount()-1, AppConstants.BAN_DURATION.length -1)]));
        userBanHistory.setCancellationCount(0);
        userBanHistoryRepository.save(userBanHistory);
    }

    private boolean isNewMonth(UserBanHistory userBanHistory) {
        if (userBanHistory.getLastBanStart() == null){
            return false;
        }
        YearMonth lastBanMonth = YearMonth.from(userBanHistory.getLastBanStart());
        YearMonth currentMonth = YearMonth.now();
        return !lastBanMonth.equals(currentMonth);
    }
}
