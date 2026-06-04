package com.example.planetarium.dto;

import lombok.Data;

@Data
public class BlogRequestDTO {
    private String title;
    private String category; // optional - not used in frontend
    private String content;
    private String imageUrl;  // optional
}
