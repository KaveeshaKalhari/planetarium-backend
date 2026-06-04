package com.example.planetarium.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booked_seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookedSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    // e.g. "A5", "B12"
    @Column(nullable = false)
    private String seatId;

    // "A", "B", "C"...
    private String seatRow;

    private int seatNumber;
}