package com.example.planetarium.controller;

import com.example.planetarium.dto.ApiResponse;
import com.example.planetarium.dto.ForgotPasswordRequestDTO;
import com.example.planetarium.dto.ResetPasswordRequestDTO;
import com.example.planetarium.dto.VerifyOtpRequestDTO;
import com.example.planetarium.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // Step 1: Request OTP — POST /api/v1/password-reset/send-otp
    @PostMapping({"/send-otp", "/forgot"})
    public ResponseEntity<ApiResponse> sendOtp(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        ApiResponse response = passwordResetService.sendOtp(request.getEmail());
        return ResponseEntity.ok(response);
    }

    // Step 2: Verify OTP — POST /api/v1/password-reset/verify-otp
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO request) {
        ApiResponse response = passwordResetService.verifyOtp(request.getEmail(), request.getOtp());
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // Step 3: Reset password — POST /api/v1/password-reset/reset
    @PostMapping("/reset")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        ApiResponse response = passwordResetService.resetPassword(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()
        );
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
