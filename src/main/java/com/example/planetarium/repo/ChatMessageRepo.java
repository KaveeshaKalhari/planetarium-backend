package com.example.planetarium.repo;

import com.example.planetarium.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long> {

    // Load all messages for a user's thread, oldest first
    List<ChatMessage> findByUsernameOrderBySentAtAsc(String username);
}
