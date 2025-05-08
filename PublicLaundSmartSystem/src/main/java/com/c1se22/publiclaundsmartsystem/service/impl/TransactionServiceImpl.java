package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.entity.Transaction;
import com.c1se22.publiclaundsmartsystem.enums.TransactionStatus;
import com.c1se22.publiclaundsmartsystem.enums.TransactionType;
import com.c1se22.publiclaundsmartsystem.exception.ResourceNotFoundException;
import com.c1se22.publiclaundsmartsystem.payload.response.TransactionDto;
import com.c1se22.publiclaundsmartsystem.repository.TransactionRepository;
import com.c1se22.publiclaundsmartsystem.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    TransactionRepository transactionRepository;
    @Override
    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public TransactionDto getTransactionById(Integer id) {
        Transaction transaction = transactionRepository.findById(id)  
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id.toString()));
        return mapToDto(transaction);
    }

    @Override
    public TransactionDto updateTransaction(Integer id, TransactionDto transactionDto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id.toString()));
        transaction.setAmount(transactionDto.getAmount());
        transaction.setStatus(transactionDto.getStatus());
        transaction.setUpdatedAt(LocalDateTime.now());
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return mapToDto(updatedTransaction);  
    }

    @Override
    public void deleteTransaction(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id.toString()));
        transactionRepository.delete(transaction);
    }

    @Override
    public List<TransactionDto> getTransactionsByUserId(Integer userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getTransactionsByUsername(String username) {
        List<Transaction> transactions = transactionRepository.findByUserUsername(username);
        return transactions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getTransactionsByStatus(TransactionStatus status) {
        List<Transaction> transactions = transactionRepository.findByStatus(status);
        return transactions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getTransactionsByTypeAndStatus(String type, String status, int page, int size,
                                                               String sortBy, String sortDir) {
        TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
        TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
        Sort sort = Sort.by(sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) ?
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Transaction> paging = transactionRepository.findByStatusAndType(
                transactionStatus, transactionType, pageable);
        return paging.getContent().stream().map(this::mapToDto).collect(Collectors.toList());
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
