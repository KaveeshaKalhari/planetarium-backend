package com.example.planetarium.service;

import com.example.planetarium.dto.PaymentRequestDTO;
import com.example.planetarium.dto.PaymentResponseDTO;
import com.example.planetarium.model.Booking;
import com.example.planetarium.model.Payment;
import com.example.planetarium.repo.BookingRepo;
import com.example.planetarium.repo.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private BookingRepo bookingRepo;

    // Called from PaymentPage after user fills card details
    public PaymentResponseDTO processPayment(PaymentRequestDTO request, Integer userId) {
        Booking booking = bookingRepo.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Ensure the booking belongs to this user
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (!"PENDING".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is not in a payable state (status: " + booking.getStatus() + ")");
        }

        // In a real system, you'd call a payment gateway (Stripe, PayHere, etc.) here.
        // For now we simulate a successful payment.
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("SUCCESS");
        payment.setTransactionReference("TXN" + System.currentTimeMillis());
        payment.setCardLastFour(request.getCardLastFour());
        payment.setCardHolderName(request.getCardHolderName());
        payment = paymentRepo.save(payment);

        // Confirm the booking
        booking.setStatus("CONFIRMED");
        bookingRepo.save(booking);

        return toResponseDTO(payment);
    }

    // Refund — marks payment as REFUNDED and booking as CANCELLED
    public PaymentResponseDTO refundPayment(Long bookingId, Integer userId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        Payment payment = paymentRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("No payment found for this booking"));

        if (!"SUCCESS".equals(payment.getStatus())) {
            throw new RuntimeException("Payment is not in a refundable state");
        }

        payment.setStatus("REFUNDED");
        booking.setStatus("CANCELLED");
        // Restore available seats
        booking.getShow().setAvailableSeats(
                booking.getShow().getAvailableSeats() + booking.getNumberOfSeats()
        );

        paymentRepo.save(payment);
        bookingRepo.save(booking);
        return toResponseDTO(payment);
    }

    public PaymentResponseDTO getPaymentForBooking(Long bookingId) {
        Payment payment = paymentRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return toResponseDTO(payment);
    }

    // ---- Helper ----

    private PaymentResponseDTO toResponseDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setBookingReference(payment.getBooking().getBookingReference());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setPaidAt(payment.getPaidAt() != null ?
                payment.getPaidAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) : "");
        return dto;
    }
}