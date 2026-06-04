package com.example.planetarium.controller;

import com.example.planetarium.dto.ApiResponse;
import com.example.planetarium.dto.PaymentRequestDTO;
import com.example.planetarium.dto.PaymentResponseDTO;
import com.example.planetarium.service.PaymentService;
import com.example.planetarium.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JwtUtil jwtUtil;

    // Called from PaymentPage after user submits card details
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(
            @RequestBody PaymentRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserId(authHeader);
            PaymentResponseDTO response = paymentService.processPayment(request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // Get payment details for a booking (for confirmation screen)
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getPayment(@PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(paymentService.getPaymentForBooking(bookingId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // User requests refund (cancel + refund together)
    @PostMapping("/refund/{bookingId}")
    public ResponseEntity<?> refundPayment(
            @PathVariable Long bookingId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserId(authHeader);
            PaymentResponseDTO response = paymentService.refundPayment(bookingId, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    private Integer extractUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.extractUserId(token);
    }
}