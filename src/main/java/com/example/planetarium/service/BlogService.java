package com.example.planetarium.service;

import com.example.planetarium.dto.BlogRequestDTO;
import com.example.planetarium.dto.BlogResponseDTO;
import com.example.planetarium.model.Blog;
import com.example.planetarium.model.User;
import com.example.planetarium.repo.BlogRepo;
import com.example.planetarium.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BlogService {

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NotificationService notificationService;

    // ── PUBLIC ──────────────────────────────────────────────

    public List<BlogResponseDTO> getApprovedBlogs() {
        return blogRepo.findByStatusOrderBySubmittedAtDesc("APPROVED")
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── USER ─────────────────────────────────────────────────

    public BlogResponseDTO submitBlog(BlogRequestDTO request, Integer userId) {
        User author = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setCategory(request.getCategory());
        blog.setContent(request.getContent());
        blog.setImageUrl(request.getImageUrl());
        blog.setAuthor(author);
        blog.setStatus("PENDING");

        return toDTO(blogRepo.save(blog));
    }

    public List<BlogResponseDTO> getMyBlogs(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return blogRepo.findByAuthorOrderBySubmittedAtDesc(user)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── ADMIN ─────────────────────────────────────────────────

    public List<BlogResponseDTO> getPendingBlogs() {
        return blogRepo.findByStatusOrderBySubmittedAtAsc("PENDING")
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<BlogResponseDTO> getAllBlogs() {
        return blogRepo.findAllByOrderBySubmittedAtDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BlogResponseDTO approveBlog(Long blogId, String adminUsername) {
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        blog.setStatus("APPROVED");
        blog.setReviewedAt(LocalDateTime.now());
        blog.setReviewedBy(adminUsername);

        BlogResponseDTO result = toDTO(blogRepo.save(blog));

        // Send in-app notification instead of email
        notificationService.notifyBlogApproved(blog.getAuthor(), blog.getTitle());

        return result;
    }

    public BlogResponseDTO rejectBlog(Long blogId, String adminUsername, String rejectionReason) {
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        blog.setStatus("REJECTED");
        blog.setReviewedAt(LocalDateTime.now());
        blog.setReviewedBy(adminUsername);
        blog.setRejectionReason(rejectionReason);

        BlogResponseDTO result = toDTO(blogRepo.save(blog));

        // Send in-app notification instead of email
        notificationService.notifyBlogRejected(blog.getAuthor(), blog.getTitle(), rejectionReason);

        return result;
    }

    public void deleteBlog(Long blogId) {
        blogRepo.deleteById(blogId);
    }

    // ── HELPER ────────────────────────────────────────────────

    private BlogResponseDTO toDTO(Blog blog) {
        BlogResponseDTO dto = new BlogResponseDTO();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setCategory(blog.getCategory());
        dto.setContent(blog.getContent());
        dto.setExcerpt(blog.getExcerpt());
        dto.setImageUrl(blog.getImageUrl());
        dto.setStatus(blog.getStatus());
        dto.setAuthorName(blog.getAuthor().getUsername());
        dto.setAuthorEmail(blog.getAuthor().getEmail());
        dto.setSubmittedAt(blog.getSubmittedAt() != null
                ? blog.getSubmittedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                : "");
        dto.setReviewedAt(blog.getReviewedAt() != null
                ? blog.getReviewedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                : null);
        dto.setReviewedBy(blog.getReviewedBy());
        dto.setRejectionReason(blog.getRejectionReason());
        return dto;
    }
}