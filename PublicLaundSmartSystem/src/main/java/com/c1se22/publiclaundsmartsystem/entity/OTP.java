package com.c1se22.publiclaundsmartsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Data
@DynamicInsert
@DynamicUpdate
@Entity(name = "otps")
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Integer id;
    private String email;
    private String code;
    private Boolean isUsed;
    private Date expiryDate;

    public OTP() {
        this.expiryDate = new Date(System.currentTimeMillis() + 5 * 60 * 1000);
    }
}
