package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.annotation.Loggable;
import com.c1se22.publiclaundsmartsystem.entity.OTP;
import com.c1se22.publiclaundsmartsystem.entity.PasswordResetToken;
import com.c1se22.publiclaundsmartsystem.entity.User;
import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import com.c1se22.publiclaundsmartsystem.exception.APIException;
import com.c1se22.publiclaundsmartsystem.exception.ResourceNotFoundException;
import com.c1se22.publiclaundsmartsystem.payload.request.EmailRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.request.ResetPasswordRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.request.VerifyOTPRequestDto;
import com.c1se22.publiclaundsmartsystem.payload.response.OTPResponseDto;
import com.c1se22.publiclaundsmartsystem.payload.response.VerifyOTPResponseDto;
import com.c1se22.publiclaundsmartsystem.repository.OTPRepository;
import com.c1se22.publiclaundsmartsystem.repository.PasswordResetTokenRepository;
import com.c1se22.publiclaundsmartsystem.repository.UserRepository;
import com.c1se22.publiclaundsmartsystem.service.OTPService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class OTPServiceImpl implements OTPService {
    OTPRepository otpRepository;
    PasswordResetTokenRepository passwordResetTokenRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JavaMailSender javaMailSender;
    private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private String generateToken() {
        Random random = new Random();
        return String.format("%s-%06d", UUID.randomUUID(), random.nextInt(1000000));
    }

    @Override
    public void sendOTP(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp +
                "\nThis OTP will expire in 5 minutes.");
        javaMailSender.send(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OTPResponseDto sendOTP(EmailRequestDto emailRequestDto) {
        User user = userRepository.findByUsernameOrEmail(emailRequestDto.getEmail(), emailRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", emailRequestDto.getEmail()));

        otpRepository.deleteByEmailAndIsUsedFalse(user.getEmail());

        String otpCode = generateOTP();
        OTP otp = new OTP();
        otp.setCode(otpCode);
        otp.setEmail(emailRequestDto.getEmail());
        otp.setIsUsed(false);
        otpRepository.save(otp);
        sendOTP(emailRequestDto.getEmail(), otpCode);
        return OTPResponseDto.builder()
                .isSuccess(true)
                .message("Success")
                .build();
    }

    @Override
    public VerifyOTPResponseDto verifyOTP(VerifyOTPRequestDto verifyOTPRequestDto) {
        OTP otp = otpRepository.findByEmailAndCode(verifyOTPRequestDto.getEmail(),
                verifyOTPRequestDto.getOtp()).orElseThrow(
                        () -> new ResourceNotFoundException("OTP", "code", verifyOTPRequestDto.getOtp()));
        if (otp.getIsUsed()) {
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.USED_OTP);
        }
        if (otp.getExpiryDate().before(new Date())) {
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.OTP_EXPIRED);
        }
        User user = userRepository.findByUsernameOrEmail(verifyOTPRequestDto.getEmail(), verifyOTPRequestDto.getEmail())
                .orElseThrow(
                    () -> new ResourceNotFoundException("User", "email", verifyOTPRequestDto.getEmail()));
        String token = generateToken();
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUserId(user.getId())
                .orElse(null);
        if (passwordResetToken == null) {
            passwordResetToken = new PasswordResetToken();
            passwordResetToken.setUser(user);
        }
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        passwordResetTokenRepository.save(passwordResetToken);
        otp.setIsUsed(true);
        otpRepository.save(otp);
        return VerifyOTPResponseDto.builder()
                .isSuccess(true)
                .resetToken(token)
                .build();
    }

    @Override
    @Loggable
    public boolean resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        User user = userRepository.findByUsernameOrEmail(resetPasswordRequestDto.getEmail(), resetPasswordRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", resetPasswordRequestDto.getEmail()));
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(
                resetPasswordRequestDto.getResetToken()).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Password Reset Token", "token", resetPasswordRequestDto.getResetToken()));
        if (passwordResetToken.getExpiryDate().before(new Date())) {
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.RESET_TOKEN_EXPIRED);
        }
        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userRepository.save(user);
        log.info("Password reset for user: " + user.getEmail());
        return true;
    }
}
