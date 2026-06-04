package com.example.planetarium.repo;

import com.example.planetarium.model.Blog;
import com.example.planetarium.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepo extends JpaRepository<Blog, Long> {

    // Public blog page — only show approved blogs, newest first
    List<Blog> findByStatusOrderBySubmittedAtDesc(String status);

    // Admin blog approval page — pending blogs first
    List<Blog> findByStatusOrderBySubmittedAtAsc(String status);

    // User's own submitted blogs
    List<Blog> findByAuthorOrderBySubmittedAtDesc(User author);

    // Admin — all blogs regardless of status
    List<Blog> findAllByOrderBySubmittedAtDesc();
}
