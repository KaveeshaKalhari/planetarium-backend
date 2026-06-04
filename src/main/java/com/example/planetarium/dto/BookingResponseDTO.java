package com.example.planetarium.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookingResponseDTO {
    private Long id;
    private String bookingReference;
    private Long showId;
    private LocalDate showDate;
    private String showTime;
    private String audienceType;
    private String language;
    private List<String> seatIds;
    private int numberOfSeats;
    private double subtotal;
    private double tax;
    private double serviceFee;
    private double totalAmount;
    private String status;
    private String createdAt;
}