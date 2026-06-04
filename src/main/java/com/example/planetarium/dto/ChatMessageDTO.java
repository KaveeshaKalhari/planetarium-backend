package com.example.planetarium.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    private Long id;

    // "user" or "admin"
    private String sender;

    private String text;

    private LocalDateTime sentAt;

    private String username;

    private String bookingDate;
    private String bookingTime;
    private String bookingLanguage;
}
