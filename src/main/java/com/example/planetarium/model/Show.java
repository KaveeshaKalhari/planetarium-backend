package com.example.planetarium.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "shows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "Public Program" or "School Program"
    @Column(nullable = false)
    private String audienceType;

    @Column(nullable = false)
    private double pricePerSeat;

    // "UPCOMING" or "CANCELLED"
    @Column(nullable = false)
    private String status = "UPCOMING";

    @Column(name = "session_type")
    private String sessionType;

    @Column(name = "show_date")
    private LocalDate showDate;

    @Column(name = "show_time")
    private String showTime;

    @Column(name = "program_type")
    private String programType;

    private String language;
    private String title;
    private String description;
    private String grade;

    @Column(name = "available_seats")
    private int availableSeats = 224;

    @Column(name = "total_seats")
    private int totalSeats = 224;

    @Column(name = "ticket_price")
    private double ticketPrice = 150.0;

    private int duration = 45;
}