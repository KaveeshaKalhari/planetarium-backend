package com.example.planetarium.controller;

import com.example.planetarium.dto.NotificationDTO;
import com.example.planetarium.service.NotificationService;
import com.example.planetarium.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUtil jwtUtil;

    // GET /api/v1/notifications — get all notifications for the logged-in user
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(
            @RequestHeader("Authorization") String authHeader) {
        String username = extractUsername(authHeader);
        return ResponseEntity.ok(notificationService.getNotificationsForUser(username));
    }

    // GET /api/v1/notifications/unread-count
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestHeader("Authorization") String authHeader) {
        String username = extractUsername(authHeader);
        long count = notificationService.getUnreadCount(username);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // PUT /api/v1/notifications/{id}/read — mark one notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String username = extractUsername(authHeader);
        notificationService.markAsRead(id, username);
        return ResponseEntity.ok().build();
    }

    // PUT /api/v1/notifications/read-all — mark all as read
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @RequestHeader("Authorization") String authHeader) {
        String username = extractUsername(authHeader);
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok().build();
    }

    private String extractUsername(String authHeader) {
        return jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
    }
}
