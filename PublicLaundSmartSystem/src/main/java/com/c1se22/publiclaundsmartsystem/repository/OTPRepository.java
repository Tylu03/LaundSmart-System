package com.c1se22.publiclaundsmartsystem.repository;

import com.c1se22.publiclaundsmartsystem.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Integer> {
    void deleteByEmailAndIsUsedFalse(String email);
    Optional<OTP> findByEmailAndCode(String email, String code);
}
