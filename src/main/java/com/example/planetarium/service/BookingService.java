package com.example.planetarium.service;

import com.example.planetarium.dto.BookingRequestDTO;
import com.example.planetarium.dto.BookingResponseDTO;
import com.example.planetarium.model.*;
import com.example.planetarium.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {

    // ── Injected from application.properties ─────────────────────────────────
    @Value("${booking.tax.rate:0.07}")
    private double taxRate;

    @Value("${booking.service.fee:2.50}")
    private double serviceFee;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private BookedSeatRepo bookedSeatRepo;

    @Autowired
    private ShowRepo showRepo;

    @Autowired
    private UserRepo userRepo;

    // ── Called from ReviewOrderPage → proceeds to PaymentPage ────────────────
    public BookingResponseDTO createBooking(BookingRequestDTO request, Integer userId) {

        // 1. Load user and show
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Show show = showRepo.findById(request.getShowId())
                .orElseThrow(() -> new RuntimeException("Show not found"));

        // 2. Validate show is still available
        if (!"UPCOMING".equals(show.getStatus())) {
            throw new RuntimeException("This show is no longer available");
        }

        // 3. Validate requested seats are not already taken
        for (String seatId : request.getSelectedSeatIds()) {
            if (bookedSeatRepo.existsByShowAndSeatId(show, seatId)) {
                throw new RuntimeException(
                        "Seat " + seatId + " is already booked. Please refresh and try again.");
            }
        }

        // 4. Validate enough seats remain
        int requestedCount = request.getSelectedSeatIds().size();
        if (show.getAvailableSeats() < requestedCount) {
            throw new RuntimeException(
                    "Not enough seats available. Only " + show.getAvailableSeats() + " remaining.");
        }

        // 5. Calculate pricing (uses injected rates, not hardcoded constants)
        double subtotal = Math.round(requestedCount * show.getPricePerSeat() * 100.0) / 100.0;
        double tax = Math.round(subtotal * taxRate * 100.0) / 100.0;
        double total = Math.round((subtotal + tax + serviceFee) * 100.0) / 100.0;

        // 6. Generate booking reference
        String reference = generateReference();

        // 7. Create booking (status = PENDING until payment confirmed)
        Booking booking = new Booking();
        booking.setBookingReference(reference);
        booking.setUser(user);
        booking.setShow(show);
        booking.setNumberOfSeats(requestedCount);
        booking.setSubtotal(subtotal);
        booking.setTax(tax);
        booking.setServiceFee(serviceFee);
        booking.setTotalAmount(total);
        booking.setStatus("PENDING");
        booking.setCustomerEmail(user.getEmail());
        booking.setCustomerName(user.getUsername());
        booking = bookingRepo.save(booking);

        // 8. Save booked seats
        List<BookedSeat> seats = new ArrayList<>();
        for (String seatId : request.getSelectedSeatIds()) {
            BookedSeat bs = new BookedSeat();
            bs.setBooking(booking);
            bs.setShow(show);
            bs.setSeatId(seatId);

            // Parse e.g. "A5" → row="A", number=5
            if (seatId != null && seatId.length() >= 2) {
                bs.setSeatRow(String.valueOf(seatId.charAt(0)));
                try {
                    bs.setSeatNumber(Integer.parseInt(seatId.substring(1)));
                } catch (NumberFormatException ignored) {
                }
            }
            seats.add(bs);
        }
        bookedSeatRepo.saveAll(seats);
        booking.setSeats(seats);

        // 9. Decrement available seats on the show
        show.setAvailableSeats(show.getAvailableSeats() - requestedCount);
        showRepo.save(show);

        return toResponseDTO(booking);
    }

    // ── User views their own bookings (My Reservations page) ─────────────────
    public List<BookingResponseDTO> getMyBookings(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepo.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── Cancel a booking ─────────────────────────────────────────────────────
    public BookingResponseDTO cancelBooking(Long bookingId, Integer userId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: this booking belongs to another user");
        }
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already cancelled");
        }

        // FIX: delete the booked seat rows so those seats are freed for new bookings
        if (booking.getSeats() != null && !booking.getSeats().isEmpty()) {
            bookedSeatRepo.deleteAll(booking.getSeats());
            booking.getSeats().clear();
        }

        booking.setStatus("CANCELLED");

        // Restore available seats on the show
        Show show = booking.getShow();
        show.setAvailableSeats(show.getAvailableSeats() + booking.getNumberOfSeats());
        showRepo.save(show);

        return toResponseDTO(bookingRepo.save(booking));
    }

    // ── Admin: get all bookings ───────────────────────────────────────────────
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepo.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Generates a unique reference like PB2026053000001.
     * Uses a timestamp suffix to avoid collisions when bookingRepo.count()
     * can lag in concurrent transactions.
     */
    private String generateReference() {
        String datePart = java.time.LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // Use nano-second component to reduce collision chance in concurrent requests
        long nanoSuffix = System.nanoTime() % 100_000L;
        long count = bookingRepo.count() + 1;
        return String.format("PB%s%05d", datePart, count + nanoSuffix % 100);
    }

    BookingResponseDTO toResponseDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setShowId(booking.getShow().getId());
        dto.setShowDate(booking.getShow().getShowDate());
        dto.setShowTime(booking.getShow().getShowTime());
        dto.setAudienceType(booking.getShow().getAudienceType());
        dto.setLanguage(booking.getShow().getLanguage());
        dto.setSeatIds(booking.getSeats() == null ? List.of()
                : booking.getSeats().stream()
                        .map(BookedSeat::getSeatId)
                        .collect(Collectors.toList()));
        dto.setNumberOfSeats(booking.getNumberOfSeats());
        dto.setSubtotal(booking.getSubtotal());
        dto.setTax(booking.getTax());
        dto.setServiceFee(booking.getServiceFee());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt() != null
                ? booking.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                : "");
        return dto;
    }
}