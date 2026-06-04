package com.example.planetarium.service;

import com.example.planetarium.dto.ChatMessageDTO;
import com.example.planetarium.model.ChatMessage;
import com.example.planetarium.repo.ChatMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepo chatMessageRepo;

    /** Save a message sent by the logged-in user */
    public ChatMessageDTO saveUserMessage(String username, ChatMessageDTO dto) {
        ChatMessage msg = new ChatMessage();
        msg.setSender("user");
        msg.setText(dto.getText());
        msg.setUsername(username);
        msg.setSentAt(LocalDateTime.now());
        msg.setBookingDate(dto.getBookingDate());
        msg.setBookingTime(dto.getBookingTime());
        msg.setBookingLanguage(dto.getBookingLanguage());
        return toDTO(chatMessageRepo.save(msg));
    }

    /** Save a reply sent by admin */
    public ChatMessageDTO saveAdminReply(String targetUsername, ChatMessageDTO dto) {
        ChatMessage msg = new ChatMessage();
        msg.setSender("admin");
        msg.setText(dto.getText());
        msg.setUsername(targetUsername);   // stored under the user's thread
        msg.setSentAt(LocalDateTime.now());
        return toDTO(chatMessageRepo.save(msg));
    }

    /** Get full conversation thread for a user */
    public List<ChatMessageDTO> getConversation(String username) {
        return chatMessageRepo.findByUsernameOrderBySentAtAsc(username)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** Admin: get all distinct usernames that have open threads */
    public List<String> getAllChatUsernames() {
        return chatMessageRepo.findAll()
                .stream()
                .map(ChatMessage::getUsername)
                .distinct()
                .collect(Collectors.toList());
    }

    private ChatMessageDTO toDTO(ChatMessage msg) {
        return new ChatMessageDTO(
                msg.getId(),
                msg.getSender(),
                msg.getText(),
                msg.getSentAt(),
                msg.getUsername(),
                msg.getBookingDate(),
                msg.getBookingTime(),
                msg.getBookingLanguage()
        );
    }
}
