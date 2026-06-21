package com.example.planetarium.service;

import com.example.planetarium.dto.NotificationDTO;
import com.example.planetarium.model.Notification;
import com.example.planetarium.model.User;
import com.example.planetarium.repo.NotificationRepo;
import com.example.planetarium.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;

    // Called by BlogService when a blog is approved
    public void notifyBlogApproved(User user, String blogTitle) {
        Notification n = new Notification();
        n.setUser(user);
        n.setType("BLOG_APPROVED");
        n.setTitle("Blog Approved!");
        n.setMessage("Your blog \"" + blogTitle + "\" has been approved and is now live.");
        n.setRead(false);
        notificationRepo.save(n);
    }

    // Called by BlogService when a blog is rejected
    public void notifyBlogRejected(User user, String blogTitle, String rejectionReason) {
        Notification n = new Notification();
        n.setUser(user);
        n.setType("BLOG_REJECTED");
        n.setTitle("Blog Not Approved");
        String message = "Your blog \"" + blogTitle + "\" was not approved at this time.";
        if (rejectionReason != null && !rejectionReason.trim().isEmpty()) {
            message += " Reason: " + rejectionReason.trim();
        }
        n.setMessage(message);
        n.setRead(false);
        notificationRepo.save(n);
    }

    // Called by PaymentService when a booking payment succeeds
    public void notifyBookingConfirmed(User user, String showTitle, String bookingReference) {
        Notification n = new Notification();
        n.setUser(user);
        n.setType("BOOKING_CONFIRMED");
        n.setTitle("Booking Confirmed!");
        n.setMessage("Your booking for \"" + showTitle + "\" (Ref: " + bookingReference
                + ") is confirmed. Check your email for your ticket.");
        n.setRead(false);
        notificationRepo.save(n);
    }

    // Get all notifications for the logged-in user
    public List<NotificationDTO> getNotificationsForUser(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepo.findByUserOrderByCreatedAtDesc(user)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Mark a single notification as read
    public void markAsRead(Long notificationId, String username) {
        Notification n = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        // Safety: ensure the notification belongs to the requesting user
        if (!n.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied");
        }
        n.setRead(true);
        notificationRepo.save(n);
    }

    // Mark all notifications as read for a user
    public void markAllAsRead(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Notification> unread = notificationRepo.findByUserOrderByCreatedAtDesc(user)
                .stream().filter(n -> !n.isRead()).collect(Collectors.toList());
        unread.forEach(n -> n.setRead(true));
        notificationRepo.saveAll(unread);
    }

    // Unread count for navbar badge
    public long getUnreadCount(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepo.countByUserAndIsRead(user, false);
    }

    private NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a")));
        return dto;
    }
}