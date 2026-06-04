package com.example.planetarium.dto;

import lombok.Data;

@Data
public class BlogResponseDTO {
    private Long id;
    private String title;
    private String category;
    private String content;
    private String excerpt;
    private String imageUrl;
    private String status; // "PENDING", "APPROVED", "REJECTED"
    private String authorName; // username of who wrote it
    private String authorEmail;
    private String submittedAt;
    private String reviewedAt;
    private String reviewedBy;
    private String rejectionReason;
}
