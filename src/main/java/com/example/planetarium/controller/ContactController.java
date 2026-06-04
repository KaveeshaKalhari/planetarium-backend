package com.example.planetarium.controller;

import com.example.planetarium.dto.ApiResponse;
import com.example.planetarium.dto.ContactRequestDTO;
import com.example.planetarium.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    // Public — submit contact form (sends email only, no DB)
    @PostMapping
    public ResponseEntity<?> submitMessage(@Valid @RequestBody ContactRequestDTO request) {
        try {
            contactService.submitMessage(request);
            return ResponseEntity.ok(new ApiResponse(true, "Message sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to send message. Please try again."));
        }
    }
}