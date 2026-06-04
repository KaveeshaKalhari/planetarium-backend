package com.example.planetarium.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // Optional — category removed from frontend form
    @Column(nullable = true)
    private String category;

    // TEXT — no length limit, supports long blog content
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // TEXT — auto-generated first 200 chars of content
    @Column(columnDefinition = "TEXT")
    private String excerpt;

    // TEXT — base64 image strings are very long, VARCHAR(255) is not enough
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    // "PENDING", "APPROVED", "REJECTED"
    @Column(nullable = false)
    private String status = "PENDING";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    // Admin who approved/rejected
    private String reviewedBy;

    // Reason provided by admin when rejecting
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        // Auto-generate excerpt from content
        if (content != null && excerpt == null) {
            excerpt = content.length() > 200
                    ? content.substring(0, 200) + "..."
                    : content;
        }
    }
}