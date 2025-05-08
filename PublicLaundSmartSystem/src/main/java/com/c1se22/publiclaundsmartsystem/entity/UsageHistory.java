package com.c1se22.publiclaundsmartsystem.entity;

import com.c1se22.publiclaundsmartsystem.enums.UsageHistoryStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_histories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
public class UsageHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Integer usageId;
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "cost", precision = 10, scale = 2, nullable = false)
    private BigDecimal cost;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UsageHistoryStatus status;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "machine_id")
    @JsonIgnore
    private Machine machine;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private WashingType washingType;
}
