package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.annotation.Loggable;
import com.c1se22.publiclaundsmartsystem.entity.*;
import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import com.c1se22.publiclaundsmartsystem.enums.TransactionStatus;
import com.c1se22.publiclaundsmartsystem.enums.TransactionType;
import com.c1se22.publiclaundsmartsystem.exception.APIException;
import com.c1se22.publiclaundsmartsystem.exception.ResourceNotFoundException;
import com.c1se22.publiclaundsmartsystem.payload.request.OwnerWithdrawInfoRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.response.TransactionDto;
import com.c1se22.publiclaundsmartsystem.repository.*;
import com.c1se22.publiclaundsmartsystem.service.OwnerService;
import com.c1se22.publiclaundsmartsystem.util.AppConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class OwnerServiceImpl implements OwnerService{
    UserRepository userRepository;
    RoleRepository roleRepository;
    MachineRepository machineRepository;
    UsageHistoryRepository usageHistoryRepository;
    OwnerWithdrawInfoRepository ownerWithdrawInfoRepository;
    TransactionRepository transactionRepository;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserToOwner(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(
                ()-> new ResourceNotFoundException("User", "username", username));
        Role role = roleRepository.findByName("ROLE_OWNER").orElseThrow(
                ()-> new ResourceNotFoundException("Role", "name", "ROLE_OWNER"));
        user.getRoles().add(role);
        userRepository.save(user);
        OwnerWithdrawInfo ownerWithdrawInfo = OwnerWithdrawInfo.builder()
                .bankName("")
                .accountNumber("")
                .accountName("")
                .lastWithdrawDate(null)
                .owner(user)
                .withdrawAmount(BigDecimal.ZERO)
                .build();
        ownerWithdrawInfoRepository.save(ownerWithdrawInfo);
        log.info("User {} is updated to owner", username);
    }

    @Override
    public BigDecimal getTotalRevenue() {
        Integer ownerId = getOwner().getId();
        List<Machine> machines = machineRepository.findMachinesByOwnerId(ownerId);
        return usageHistoryRepository.sumCostByMachines(machines);
    }

    @Override
    public BigDecimal getRevenueBeforeDate(LocalDate date) {
        Integer ownerId = getOwner().getId();
        List<Machine> machines = machineRepository.findMachinesByOwnerId(ownerId);
        LocalDateTime end = date.atStartOfDay();
        return usageHistoryRepository.sumCostByMachineInAndStartTimeBefore(machines, end);
    }

    @Override
    public BigDecimal getRevenueByMonth(int month) {
        Integer ownerId = getOwner().getId();
        List<Machine> machines = machineRepository.findMachinesByOwnerId(ownerId);
        LocalDateTime start = LocalDateTime.now().withMonth(month).withDayOfMonth(1);
        LocalDateTime end = start.plusMonths(1).minusDays(1);
        return usageHistoryRepository.sumCostByMachineInAndStartTimeBetween(machines, start, end);
    }

    @Override
    public BigDecimal getRevenueByYear(int year) {
        Integer ownerId = getOwner().getId();
        List<Machine> machines = machineRepository.findMachinesByOwnerId(ownerId);
        LocalDateTime start = LocalDateTime.now().withYear(year).withMonth(1).withDayOfMonth(1);
        LocalDateTime end = start.plusYears(1).minusDays(1);
        return usageHistoryRepository.sumCostByMachineInAndStartTimeBetween(machines, start, end);
    }

    @Override
    public BigDecimal getRevenueByMonthAndYear(int month, int year) {
        Integer ownerId = getOwner().getId();
        List<Machine> machines = machineRepository.findMachinesByOwnerId(ownerId);
        LocalDateTime start = LocalDateTime.now().withYear(year).withMonth(month).withDayOfMonth(1);
        LocalDateTime end = start.plusMonths(1).minusDays(1);
        return usageHistoryRepository.sumCostByMachineInAndStartTimeBetween(machines, start, end);
    }

    @Override
    public BigDecimal getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        Integer ownerId = getOwner().getId();
        List<Machine> machines = machineRepository.findMachinesByOwnerId(ownerId);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atStartOfDay();
        return usageHistoryRepository.sumCostByMachineInAndStartTimeBetween(machines, start, end);
    }

    @Override
    public List<TransactionDto> getWithdrawHistory(String username, String sortDir) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(
                ()-> new ResourceNotFoundException("User", "username", username));
        if (user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_OWNER"))){
            throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN);
        }
        Sort sort = Sort.by(sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) ?
                Sort.Direction.DESC : Sort.Direction.ASC, "timestamp");
        Pageable pageable = Pageable.unpaged(sort);
        return transactionRepository.findByUserUsernameAndType(username, TransactionType.WITHDRAWAL, pageable)
                .getContent()
                .stream().map(this::mapToDto).toList();
    }

    @Override
    public Integer getNumberOfUsingByMonth(int month) {
        Integer ownerId = getOwner().getId();
        List<Machine> machines = machineRepository.findMachinesByOwnerId(ownerId);
        LocalDateTime start = LocalDateTime.now().withMonth(month).withDayOfMonth(1);
        LocalDateTime end = start.plusMonths(1).minusDays(1);
        return usageHistoryRepository.countByMachineInAndStartTimeBetween(machines, start, end);
    }

    @Override
    public Integer getNumberOfUsingByYear(int year) {
        Integer ownerId = getOwner().getId();
        List<Machine> machines = machineRepository.findMachinesByOwnerId(ownerId);
        LocalDateTime start = LocalDateTime.now().withYear(year).withMonth(1).withDayOfMonth(1);
        LocalDateTime end = start.plusYears(1).minusDays(1);
        return usageHistoryRepository.countByMachineInAndStartTimeBetween(machines, start, end);
    }

    @Override
    public Boolean updateWithdrawInfo(OwnerWithdrawInfoRequestDto requestDto) {
        User user = getOwner();
        if (user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_OWNER"))){
            throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN);
        }
        ownerWithdrawInfoRepository.findByOwnerUsername(user.getUsername()).ifPresentOrElse(
                ownerWithdrawInfo -> {
                    ownerWithdrawInfo.setBankName(requestDto.getBankName());
                    ownerWithdrawInfo.setAccountNumber(requestDto.getAccountNumber());
                    ownerWithdrawInfo.setAccountName(requestDto.getAccountName());
                    ownerWithdrawInfoRepository.save(ownerWithdrawInfo);
                },
                ()-> ownerWithdrawInfoRepository.save(OwnerWithdrawInfo.builder()
                        .bankName(requestDto.getBankName())
                        .accountNumber(requestDto.getAccountNumber())
                        .accountName(requestDto.getAccountName())
                        .lastWithdrawDate(null)
                        .owner(user)
                        .withdrawAmount(BigDecimal.ZERO)
                        .build())
        );
        return true;
    }

    @Override
    public BigDecimal getAmountCanWithdraw(String username) {
        OwnerWithdrawInfo ownerWithdrawInfo = ownerWithdrawInfoRepository.findByOwnerUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("OwnerWithdrawInfo", "ownerUsername", username));
        return ownerWithdrawInfo.getWithdrawAmount();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Loggable
    public Boolean withdraw(BigDecimal amount) {
        User user = getOwner();
        OwnerWithdrawInfo ownerWithdrawInfo = ownerWithdrawInfoRepository.findByOwnerUsername(user.getUsername())
                .orElseThrow(()-> new ResourceNotFoundException("OwnerWithdrawInfo", "ownerUsername", user.getUsername()));
        if (amount.compareTo(AppConstants.MINIMUM_WITHDRAW_AMOUNT) < 0){
            log.error("Owner {} cannot withdraw because the amount is {}", user.getUsername(), amount);
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.MINIMUM_AMOUNT,
                    AppConstants.MINIMUM_WITHDRAW_AMOUNT, amount);
        }
        if (Objects.equals(ownerWithdrawInfo.getAccountName(), "") ||
                Objects.equals(ownerWithdrawInfo.getAccountNumber(), "") ||
                Objects.equals(ownerWithdrawInfo.getBankName(), "")){
            log.error("Owner {} cannot withdraw because the account information is not completed", user.getUsername());
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.ACCOUNT_INFO);
        }
        LocalDate lastWithdrawDate = ownerWithdrawInfo.getLastWithdrawDate();
        if (lastWithdrawDate != null && lastWithdrawDate.isAfter(LocalDate.now().minusDays(AppConstants.WITHDRAW_DURATION))){
            log.error("Owner {} cannot withdraw because the last withdraw date is {}", user.getUsername(), lastWithdrawDate);
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.WITHDRAW_INTERVAL, AppConstants.WITHDRAW_DURATION);
        }
        ownerWithdrawInfo.setPendingLastWithdrawDate(LocalDate.now());
        ownerWithdrawInfo.setWithdrawAmount(ownerWithdrawInfo.getWithdrawAmount().subtract(amount));
        ownerWithdrawInfoRepository.save(ownerWithdrawInfo);
        transactionRepository.save(Transaction.builder()
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .user(user)
                .status(TransactionStatus.PENDING)
                .type(TransactionType.WITHDRAWAL)
                .updatedAt(LocalDateTime.now())
                .build());
        log.info("Owner {} withdraws {}", user.getUsername(), amount);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Loggable
    public Boolean confirmWithdraw(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
                ()-> new ResourceNotFoundException("Transaction", "id", transactionId.toString()));
        if (transaction.getStatus() != TransactionStatus.PENDING){
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.TRANSACTION_STATUS, transaction.getStatus());
        }
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        log.info("Transaction {} is confirmed", transactionId);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Loggable
    public Boolean cancelWithdraw(Integer transactionId, String description) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
                ()-> new ResourceNotFoundException("Transaction", "id", transactionId.toString()));
        if (transaction.getStatus() != TransactionStatus.PENDING){
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.TRANSACTION_STATUS, transaction.getStatus());
        }
        transaction.setStatus(TransactionStatus.CANCELLED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setDescription(description);
        transactionRepository.save(transaction);
        log.info("Transaction {} is cancelled", transactionId);
        OwnerWithdrawInfo ownerWithdrawInfo = ownerWithdrawInfoRepository.findByOwnerUsername(transaction.getUser().getUsername())
                .orElseThrow(()-> new ResourceNotFoundException("OwnerWithdrawInfo", "ownerUsername", transaction.getUser().getUsername()));
        ownerWithdrawInfo.setWithdrawAmount(ownerWithdrawInfo.getWithdrawAmount().add(transaction.getAmount()));
        ownerWithdrawInfoRepository.save(ownerWithdrawInfo);
        log.info("Owner {} is refunded {}", transaction.getUser().getUsername(), transaction.getAmount());
        return Boolean.TRUE;
    }

    private User getOwner(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.findByUsernameOrEmail(currentUsername, currentUsername).orElseThrow(
                ()-> new ResourceNotFoundException("User", "username", "owner"));
        if (user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_OWNER"))){
            throw new ResourceNotFoundException("User", "username", "owner");
        }
        return user;
    }

    private TransactionDto mapToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .timestamp(transaction.getTimestamp())
                .userId(transaction.getUser().getId())
                .userName(transaction.getUser().getUsername())
                .type(transaction.getType())
                .build();
    }
}
