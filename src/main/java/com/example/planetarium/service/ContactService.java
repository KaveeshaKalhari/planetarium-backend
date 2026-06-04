package com.example.planetarium.service;

import com.example.planetarium.dto.ContactRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    @Autowired
    private EmailService emailService;

    // Just send emails — no database saving
    public void submitMessage(ContactRequestDTO request) {

        // Send email to planetarium inbox
        emailService.sendContactEmail(
                request.getName(),
                request.getEmail(),
                request.getMessage()
        );

        // Send confirmation email back to user
        emailService.sendConfirmationEmail(
                request.getName(),
                request.getEmail()
        );
    }
}