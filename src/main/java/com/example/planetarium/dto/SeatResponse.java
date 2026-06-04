package com.example.planetarium.dto;

import com.example.planetarium.model.Seat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeatResponse {

    private Long id;

    // e.g. "A", "B", "C"
    private String row;

    // e.g. "5", "12"
    private String seatNumber;

    // Composite label the frontend uses: "A5", "B12"
    private String seatId;

    // "AVAILABLE", "HELD", "BOOKED"
    private String status;

    // Only non-null when status = "HELD" — lets the frontend show a countdown
    private LocalDateTime heldUntil;

    public static SeatResponse from(Seat s) {
        SeatResponse r = new SeatResponse();
        r.setId(s.getId());
        r.setRow(s.getRowName());
        r.setSeatNumber(s.getSeatNumber());

        // Build composite seat label so frontend doesn't have to
        String composite = (s.getRowName() != null ? s.getRowName() : "")
                         + (s.getSeatNumber() != null ? s.getSeatNumber() : "");
        r.setSeatId(composite);

        r.setStatus(s.getStatus());
        r.setHeldUntil(s.getHeldUntil());
        return r;
    }
}