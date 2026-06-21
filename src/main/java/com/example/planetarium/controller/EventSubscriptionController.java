package com.example.planetarium.controller;

import com.example.planetarium.service.EventSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/events/subscribe")
@RequiredArgsConstructor
public class EventSubscriptionController {

    private final EventSubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Map<String, String>> subscribe(@RequestBody Map<String, Object> body) {
        try {
            String email = (String) body.get("email");
            boolean dayBefore = body.getOrDefault("alertDayBefore", true).equals(true);
            boolean hourBefore = body.getOrDefault("alertHourBefore", false).equals(true);

            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email is required"));
            }

            String result = subscriptionService.subscribe(email, dayBefore, hourBefore);
            return ResponseEntity.ok(Map.of("message", result));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Subscription failed: " + e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> unsubscribe(@RequestParam String email) {
        subscriptionService.unsubscribe(email);
        return ResponseEntity.ok(Map.of("message", "Unsubscribed successfully"));
    }
}
