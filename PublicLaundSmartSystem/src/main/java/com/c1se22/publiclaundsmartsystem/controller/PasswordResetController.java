package com.c1se22.publiclaundsmartsystem.controller;

import com.c1se22.publiclaundsmartsystem.payload.request.EmailRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.request.ResetPasswordRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.request.VerifyOTPRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.response.OTPResponseDto;
import com.c1se22.publiclaundsmartsystem.payload.response.VerifyOTPResponseDto;
import com.c1se22.publiclaundsmartsystem.service.OTPService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
@AllArgsConstructor
public class PasswordResetController {
    OTPService otpService;

    @PostMapping("/reset-request")
    public ResponseEntity<OTPResponseDto> resetPasswordRequest(@Valid @RequestBody EmailRequestDto emailRequestDto) {
        return ResponseEntity.ok(otpService.sendOTP(emailRequestDto));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOTPResponseDto> verifyOTP(@Valid @RequestBody VerifyOTPRequestDto otpRequestDto) {
        return ResponseEntity.ok(otpService.verifyOTP(otpRequestDto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Boolean> resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        return ResponseEntity.ok(otpService.resetPassword(resetPasswordRequestDto));
    }
}
