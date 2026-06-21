package com.example.planetarium.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_subscriptions",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
public class EventSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // true = send email 1 day before the event
    @Column(nullable = false)
    private boolean alertOneDayBefore = true;

    // true = send email 1 hour before the event
    @Column(nullable = false)
    private boolean alertOneHourBefore = false;

    @Column(nullable = false)
    private LocalDateTime subscribedAt = LocalDateTime.now();
}
