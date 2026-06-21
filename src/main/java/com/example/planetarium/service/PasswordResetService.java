package com.example.planetarium.service;

import com.example.planetarium.dto.ApiResponse;
import com.example.planetarium.model.User;
import com.example.planetarium.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class PasswordResetService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int OTP_EXPIRY_MINUTES = 10;

    // ── Step 1: Send OTP to email ──────────────────────────────────────────────
    public ApiResponse sendOtp(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        // Always return success to avoid revealing if an email is registered
        if (userOptional.isEmpty()) {
            return new ApiResponse(true, "If this email is registered, an OTP has been sent.");
        }

        User user = userOptional.get();

        // Generate 6-digit OTP
        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        // Save hashed OTP + expiry to user
        user.setResetOtp(passwordEncoder.encode(otp));
        user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        userRepo.save(user);

        // Send OTP email
        emailService.sendOtpEmail(email, otp);

        return new ApiResponse(true, "If this email is registered, an OTP has been sent.");
    }

    // ── Step 2: Verify OTP ─────────────────────────────────────────────────────
    public ApiResponse verifyOtp(String email, String otp) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            return new ApiResponse(false, "Invalid OTP.");
        }

        User user = userOptional.get();

        if (user.getResetOtp() == null || user.getResetOtpExpiry() == null) {
            return new ApiResponse(false, "No OTP requested. Please request a new one.");
        }

        if (LocalDateTime.now().isAfter(user.getResetOtpExpiry())) {
            return new ApiResponse(false, "OTP has expired. Please request a new one.");
        }

        if (!passwordEncoder.matches(otp, user.getResetOtp())) {
            return new ApiResponse(false, "Invalid OTP.");
        }

        return new ApiResponse(true, "OTP verified successfully.");
    }

    // ── Step 3: Reset password ─────────────────────────────────────────────────
    public ApiResponse resetPassword(String email, String otp, String newPassword) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            return new ApiResponse(false, "Invalid request.");
        }

        User user = userOptional.get();

        if (user.getResetOtp() == null || user.getResetOtpExpiry() == null) {
            return new ApiResponse(false, "No OTP requested. Please request a new one.");
        }

        if (LocalDateTime.now().isAfter(user.getResetOtpExpiry())) {
            return new ApiResponse(false, "OTP has expired. Please request a new one.");
        }

        if (!passwordEncoder.matches(otp, user.getResetOtp())) {
            return new ApiResponse(false, "Invalid OTP.");
        }

        // Update password and clear OTP fields
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        userRepo.save(user);

        return new ApiResponse(true, "Password reset successfully.");
    }
}
