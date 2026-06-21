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

    @Autowired
    private EmailService emailService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private NotificationService notificationService;

    // Called from PaymentPage after user fills card details
    public PaymentResponseDTO processPayment(PaymentRequestDTO request, Integer userId) {
        Booking booking = bookingRepo.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Ensure the booking belongs to this user
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (paymentRepo.findByBookingId(booking.getId()).isPresent()) {
            throw new RuntimeException("This booking has already been paid for");
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

        // Notify the user their booking is confirmed
        try {
            notificationService.notifyBookingConfirmed(
                    booking.getUser(),
                    booking.getShow().getTitle(),
                    booking.getBookingReference());
        } catch (Exception e) {
            System.err.println("Failed to create booking-confirmed notification: " + e.getMessage());
        }

        // Send QR ticket email
        try {
            String qrContent = booking.getBookingReference();
            String qrBase64 = qrCodeService.generateQRCodeBase64(qrContent);

            String seatNumbers = booking.getSeats().stream()
                    .map(s -> s.getSeatRow() + s.getSeatNumber())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("N/A");

            String showDate = booking.getShow().getShowDate() != null
                    ? booking.getShow().getShowDate().toString()
                    : "TBA";
            String showTime = booking.getShow().getShowTime() != null
                    ? booking.getShow().getShowTime().toString()
                    : "TBA";

            emailService.sendBookingTicketEmail(
                    booking.getCustomerName(),
                    booking.getCustomerEmail(),
                    booking.getBookingReference(),
                    booking.getShow().getTitle(),
                    showDate,
                    showTime,
                    booking.getNumberOfSeats(),
                    seatNumbers,
                    booking.getTotalAmount(),
                    qrBase64);
        } catch (Exception e) {
            System.err.println("Failed to send QR ticket email: " + e.getMessage());
            // Don't fail the payment if email fails
        }

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
        // Restore available seats
        booking.getShow().setAvailableSeats(
                booking.getShow().getAvailableSeats() + booking.getNumberOfSeats());

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
        dto.setPaidAt(payment.getPaidAt() != null
                ? payment.getPaidAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                : "");
        return dto;
    }
}