package com.c1se22.publiclaundsmartsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_ban_histories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
public class UserBanHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ban_id")
    private Integer id;
    @Column(name = "ban_count")
    private Integer banCount;
    @Column(name = "cancellation_count")
    private Integer cancellationCount;
    @Column(name = "last_ban_start")
    private LocalDateTime lastBanStart;
    @Column(name = "last_ban_end")
    private LocalDateTime lastBanEnd;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
