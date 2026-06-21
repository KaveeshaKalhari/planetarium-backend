package com.example.planetarium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data

public class NotificationDTO {
    private Long id;
    private String type;
    private String title;
    private String message;

    @JsonProperty("isRead")
    private boolean isRead;
    
    private String createdAt;
}
