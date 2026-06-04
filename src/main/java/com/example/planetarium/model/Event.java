package com.example.planetarium.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String eventDate;   // e.g. "July 6, 2024"
    private String startTime;   // e.g. "1:00 PM"
    private String endTime;     // e.g. "3:00 PM"

    private String type;        // "yellow" | "blue" | "red"
    private String icon;        // emoji e.g. "☀️"
    private String badge;       // e.g. "Special Event" (nullable)

    private String status;      // "upcoming" | "archived"
}
