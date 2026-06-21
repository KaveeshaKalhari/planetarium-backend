package com.example.planetarium.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlogRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 100, message = "Title must be between 10 and 100 characters")
    private String title;

    private String category;

    @NotBlank(message = "Content is required")
    @Size(min = 100, max = 5000, message = "Content must be between 100 and 5000 characters")
    private String content;

    private String imageUrl;
}