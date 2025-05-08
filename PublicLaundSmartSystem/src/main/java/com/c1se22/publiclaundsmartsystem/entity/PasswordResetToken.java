package com.c1se22.publiclaundsmartsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Data
@DynamicInsert
@DynamicUpdate
@Entity(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reset_token_id")
    private Integer id;
    private String token;
    private Date expiryDate;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public PasswordResetToken() {
        this.expiryDate = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
    }
}
