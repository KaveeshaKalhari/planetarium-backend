package com.example.planetarium.dto;

import lombok.Data;

@Data
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private String eventDate;
    private String startTime;
    private String endTime;
    private String type;
    private String icon;
    private String badge;
    private String status;
}
