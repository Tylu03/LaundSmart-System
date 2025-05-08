package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.entity.Transaction;
import com.c1se22.publiclaundsmartsystem.payload.request.OwnerWithdrawInfoRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.response.TransactionDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OwnerService {
    void updateUserToOwner(String username);
    BigDecimal getTotalRevenue();
    BigDecimal getRevenueBeforeDate(LocalDate date);
    BigDecimal getRevenueByMonth(int month);
    BigDecimal getRevenueByYear(int year);
    BigDecimal getRevenueByMonthAndYear(int month, int year);
    BigDecimal getRevenueByDateRange(LocalDate startDate, LocalDate endDate);
    List<TransactionDto> getWithdrawHistory(String username, String sortDir);
    Integer getNumberOfUsingByMonth(int month);
    Integer getNumberOfUsingByYear(int year);
    Boolean updateWithdrawInfo(OwnerWithdrawInfoRequestDto requestDto);
    BigDecimal getAmountCanWithdraw(String username);
    Boolean withdraw(BigDecimal amount);
    Boolean confirmWithdraw(Integer transactionId);
    Boolean cancelWithdraw(Integer transactionId, String description);
}
