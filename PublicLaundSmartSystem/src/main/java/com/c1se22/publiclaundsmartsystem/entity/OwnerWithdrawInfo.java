package com.c1se22.publiclaundsmartsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "owner_withdraw_infos")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class OwnerWithdrawInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_id")
    private Integer id;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "account_number")
    private String accountNumber;
    @Column(name = "account_name")
    private String accountName;
    @Column(name = "last_withdraw")
    private LocalDate lastWithdrawDate;
    @Column(name = "pending_last_withdraw")
    private LocalDate pendingLastWithdrawDate;
    @Column(name = "withdraw_amount", columnDefinition = "DECIMAL(10,2)", nullable = false)
    private BigDecimal withdrawAmount;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;
}