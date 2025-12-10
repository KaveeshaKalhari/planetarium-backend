package com.example.planetarium.controller;

import com.example.planetarium.dto.GoogleAuthDTO;
import com.example.planetarium.dto.LoginResponse;
import com.example.planetarium.service.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/auth")
public class GoogleAuthController {
    @Autowired
    private GoogleAuthService googleAuthService;

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> authenticateWithGoogle(@RequestBody GoogleAuthDTO googleAuthDTO) {
        try {
            LoginResponse response = googleAuthService.authenticateWithGoogle(googleAuthDTO.getToken());

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(false, "Google authentication failed", null, null));
        }
    }
}
