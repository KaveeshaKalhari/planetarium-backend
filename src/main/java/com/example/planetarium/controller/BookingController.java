package com.example.planetarium.controller;

import com.example.planetarium.dto.ApiResponse;
import com.example.planetarium.dto.BookingRequestDTO;
import com.example.planetarium.dto.BookingResponseDTO;
import com.example.planetarium.service.BookingService;
import com.example.planetarium.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtUtil jwtUtil;

    // User creates a new booking (called from ReviewOrderPage → proceeds to PaymentPage)
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody BookingRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserId(authHeader);
            BookingResponseDTO response = bookingService.createBooking(request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // User views their own bookings (My Reservations page)
    @GetMapping("/my")
    public ResponseEntity<?> getMyBookings(@RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserId(authHeader);
            List<BookingResponseDTO> bookings = bookingService.getMyBookings(userId);
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // User cancels their booking
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserId(authHeader);
            BookingResponseDTO response = bookingService.cancelBooking(id, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // Admin — view all bookings (BookingAnalysis page)
    @GetMapping("/admin/all")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // ---- Helper ----
    private Integer extractUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.extractUserId(token);
    }
}