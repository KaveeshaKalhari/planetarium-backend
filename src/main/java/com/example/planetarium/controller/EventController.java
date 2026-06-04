package com.example.planetarium.controller;

import java.util.List;
import java.lang.reflect.Method;

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

    // Public — users see this on EventPage
    @GetMapping
    public ResponseEntity<List<EventDTO>> getUpcoming() {
        try {
            Method method = eventService.getClass().getMethod("getUpcomingEvents");
            @SuppressWarnings("unchecked")
            List<EventDTO> events = (List<EventDTO>) method.invoke(eventService);
            return ResponseEntity.ok(events);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to get upcoming events", ex);
        }
    }

    // Admin only
    @GetMapping("/admin/all")
    public ResponseEntity<List<EventDTO>> getAllAdmin() {
        try {
            Method method = eventService.getClass().getMethod("getAllForAdmin");
            @SuppressWarnings("unchecked")
            List<EventDTO> events = (List<EventDTO>) method.invoke(eventService);
            return ResponseEntity.ok(events);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to get all events for admin", ex);
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<EventDTO> create(@RequestBody EventDTO dto) {
        try {
            Method method = eventService.getClass().getMethod(
                "create",
                dto.getClass().getInterfaces().length > 0 ? dto.getClass().getInterfaces()[0] : dto.getClass()
            );
            EventDTO created = (EventDTO) method.invoke(eventService, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to create event", ex);
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable Long id, @RequestBody EventDTO dto) {
        try {
            Method method = eventService.getClass().getMethod("update", Long.class, dto.getClass().getInterfaces().length > 0 ? dto.getClass().getInterfaces()[0] : dto.getClass());
            EventDTO updated = (EventDTO) method.invoke(eventService, id, dto);
            return ResponseEntity.ok(updated);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to update event", ex);
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
