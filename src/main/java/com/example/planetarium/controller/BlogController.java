package com.example.planetarium.controller;

import com.example.planetarium.dto.ApiResponse;
import com.example.planetarium.dto.BlogRequestDTO;
import com.example.planetarium.dto.BlogResponseDTO;
import com.example.planetarium.service.BlogService;
import com.example.planetarium.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private JwtUtil jwtUtil;

    // ── PUBLIC ──────────────────────────────────────────────────────────────

    // UserBlogPage — get all approved blogs (no auth needed)
    @GetMapping
    public ResponseEntity<List<BlogResponseDTO>> getApprovedBlogs() {
        return ResponseEntity.ok(blogService.getApprovedBlogs());
    }

    // ── USER ────────────────────────────────────────────────────────────────

    // WriteBlogPage — submit a new blog (must be logged in)
    @PostMapping
    public ResponseEntity<?> submitBlog(
            @RequestBody BlogRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserId(authHeader);
            BlogResponseDTO response = blogService.submitBlog(request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // User views their own blogs
    @GetMapping("/my")
    public ResponseEntity<?> getMyBlogs(
            @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserId(authHeader);
            return ResponseEntity.ok(blogService.getMyBlogs(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ── ADMIN ────────────────────────────────────────────────────────────────

    // BlogApproval page — get all PENDING blogs
    @GetMapping("/admin/pending")
    public ResponseEntity<List<BlogResponseDTO>> getPendingBlogs() {
        return ResponseEntity.ok(blogService.getPendingBlogs());
    }

    // Admin — get all blogs (all statuses)
    @GetMapping("/admin/all")
    public ResponseEntity<List<BlogResponseDTO>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    // Admin approves a blog
    @PutMapping("/admin/{id}/approve")
    public ResponseEntity<?> approveBlog(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String adminUsername = extractUsername(authHeader);
            return ResponseEntity.ok(blogService.approveBlog(id, adminUsername));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // Admin rejects a blog
    @PutMapping("/admin/{id}/reject")
    public ResponseEntity<?> rejectBlog(
            @PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String adminUsername = extractUsername(authHeader);
            String reason = (body != null && body.containsKey("reason")) ? body.get("reason") : null;
            return ResponseEntity.ok(blogService.rejectBlog(id, adminUsername, reason));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // Admin deletes a blog
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Long id) {
        try {
            blogService.deleteBlog(id);
            return ResponseEntity.ok(new ApiResponse(true, "Blog deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ── HELPERS ─────────────────────────────────────────────────────────────

    private Integer extractUserId(String authHeader) {
        return jwtUtil.extractUserId(authHeader.replace("Bearer ", ""));
    }

    private String extractUsername(String authHeader) {
        return jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
    }
}