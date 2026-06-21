package com.example.planetarium.repo;

import com.example.planetarium.model.Notification;
import com.example.planetarium.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

    // All notifications for a user, newest first
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Count unread notifications for a user
    long countByUserAndIsRead(User user, boolean isRead);
}