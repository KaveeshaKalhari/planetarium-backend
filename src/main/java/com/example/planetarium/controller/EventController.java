package com.example.planetarium.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planetarium.dto.EventDTO;
import com.example.planetarium.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getUpcoming() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<EventDTO>> getAllAdmin() {
        return ResponseEntity.ok(eventService.getAllForAdmin());
    }

    @PostMapping("/admin")
    public ResponseEntity<EventDTO> create(@RequestBody EventDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(dto));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable Long id, @RequestBody EventDTO dto) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}