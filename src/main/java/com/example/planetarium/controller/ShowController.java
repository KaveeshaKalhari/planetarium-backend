package com.example.planetarium.controller;

import com.example.planetarium.dto.ApiResponse;
import com.example.planetarium.dto.ShowDTO;
import com.example.planetarium.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/shows")
public class ShowController {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage()));
    }

    @Autowired
    private ShowService showService;

    // Public — used by ShowAvailability page (next 21 days)
    @GetMapping
    public ResponseEntity<List<ShowDTO>> getUpcomingShows() {
        return ResponseEntity.ok(showService.getUpcomingShows());
    }

    // Public — used by SeatSelectionPage to get booked seats for a show
    @GetMapping("/{id}")
    public ResponseEntity<ShowDTO> getShowWithSeats(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowWithSeats(id));
    }

    // Admin — get all shows (for EventManagement / admin overview)
    @GetMapping("/admin/all")
    public ResponseEntity<List<ShowDTO>> getAllForAdmin() {
        return ResponseEntity.ok(showService.getAllShowsForAdmin());
    }

    // Admin — create a new show
    @PostMapping("/admin")
    public ResponseEntity<ShowDTO> createShow(@RequestBody ShowDTO dto) {
        ShowDTO created = showService.createShow(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Admin — update a show
    @PutMapping("/admin/{id}")
    public ResponseEntity<ShowDTO> updateShow(@PathVariable Long id, @RequestBody ShowDTO dto) {
        ShowDTO updated = showService.updateShow(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Admin — delete a show
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {
        showService.deleteShow(id);
        return ResponseEntity.noContent().build();
    }
}