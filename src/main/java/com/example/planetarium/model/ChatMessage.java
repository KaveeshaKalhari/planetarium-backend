package com.example.planetarium.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "user" or "admin"
    @Column(nullable = false)
    private String sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    // The user's username who owns this conversation thread
    @Column(name = "username", nullable = false)
    private String username;

    // Optional: tie a chat to a specific booking/show session
    @Column(name = "booking_date")
    private String bookingDate;

    @Column(name = "booking_time")
    private String bookingTime;

    @Column(name = "booking_language")
    private String bookingLanguage;
}
