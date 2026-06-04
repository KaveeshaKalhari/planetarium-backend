package com.example.planetarium.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    // "SUCCESS", "FAILED", "REFUNDED"
    @Column(name = "payment_status", nullable = false)
    private String status;

    @Column(name = "transaction_reference", nullable = false)
    private String transactionReference;

    // Last 4 digits only — never store full card number
    private String cardLastFour;
    private String cardHolderName;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        paidAt = LocalDateTime.now();
    }
}