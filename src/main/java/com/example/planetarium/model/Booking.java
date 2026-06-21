package com.example.planetarium.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. PB2026031500001
    @Column(unique = true, nullable = false)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BookedSeat> seats;

    @Column(nullable = false)
    private int numberOfSeats;

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double totalAmount;

    // "PENDING", "CONFIRMED", "CANCELLED"
    @Column(name = "booking_status", nullable = false)
    private String status = "PENDING";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}