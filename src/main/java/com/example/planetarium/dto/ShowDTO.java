package com.example.planetarium.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ShowDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate showDate;
    private String showTime;          // "morning" or "afternoon"
    private String audienceType;      // "School Program" or "Public Program"
    private String programType;
    private String language;
    private String grade;
    private int totalSeats;
    private int availableSeats;
    private double pricePerSeat;
    private String status;
    private int duration;
    private List<String> bookedSeatIds;
}