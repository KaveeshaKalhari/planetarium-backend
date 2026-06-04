package com.example.planetarium.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private UserDTO user;
    private String token;
    private String role;

    public LoginResponse(boolean success, String message, UserDTO user, String token) {
        this.success = success;
        this.message = message;
        this.user    = user;
        this.token   = token;
        this.role    = (user != null) ? user.getRole() : null;
    }
}