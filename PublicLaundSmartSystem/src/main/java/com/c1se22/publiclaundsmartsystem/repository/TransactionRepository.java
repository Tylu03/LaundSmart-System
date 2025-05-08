package com.c1se22.publiclaundsmartsystem.repository;

import com.c1se22.publiclaundsmartsystem.entity.Transaction;
import com.c1se22.publiclaundsmartsystem.enums.TransactionStatus;
import com.c1se22.publiclaundsmartsystem.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserId(Integer userId);
    List<Transaction> findByUserUsername(String username);
    List<Transaction> findByStatus(TransactionStatus status);
    Transaction findByPaymentId(String paymentId);
    Page<Transaction> findByStatusAndType(TransactionStatus status, TransactionType type, Pageable pageable);
    Page<Transaction> findByUserUsernameAndType(String username, TransactionType type, Pageable pageable);
}
