package com.example.planetarium.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequestDTO {
    private Long showId;
    private List<String> selectedSeatIds; // e.g. ["A5", "A6", "B3"]
    private SchoolFormDTO schoolForm;
}