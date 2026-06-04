package com.example.planetarium.controller;

import com.example.planetarium.dto.SeatResponse;
import com.example.planetarium.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/show/{showId}")
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable Long showId) {
        List<SeatResponse> seats = seatService.getSeatsForShow(showId)
                .stream().map(SeatResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(seats);
    }
}