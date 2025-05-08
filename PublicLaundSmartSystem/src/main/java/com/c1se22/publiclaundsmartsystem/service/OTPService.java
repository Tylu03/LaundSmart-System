package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.payload.request.EmailRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.request.ResetPasswordRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.request.VerifyOTPRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.response.OTPResponseDto;
import com.c1se22.publiclaundsmartsystem.payload.response.VerifyOTPResponseDto;

public interface OTPService {
    void sendOTP(String email, String otp);
    OTPResponseDto sendOTP(EmailRequestDto emailRequestDto);
    VerifyOTPResponseDto verifyOTP(VerifyOTPRequestDto verifyOTPRequestDto);
    boolean resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);
}
