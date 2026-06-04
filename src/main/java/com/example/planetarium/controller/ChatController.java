package com.example.planetarium.controller;

import com.example.planetarium.dto.ChatMessageDTO;
import com.example.planetarium.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * User sends a message to admin.
     * POST /api/v1/chat/send
     */

    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            Authentication auth,
            @RequestBody ChatMessageDTO dto) {
        String username = auth.getName();
        ChatMessageDTO saved = chatService.saveUserMessage(username, dto);
        return ResponseEntity.ok(saved);
    }

    /**
     * User loads their own conversation thread.
     * GET /api/v1/chat/messages
     */
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMyMessages(Authentication auth) {
        String username = auth.getName();
        return ResponseEntity.ok(chatService.getConversation(username));
    }

    /**
     * Admin: reply to a specific user's thread.
     * POST /api/v1/chat/admin/reply/{username}
     */
    @PostMapping("/admin/reply/{username}")
    public ResponseEntity<ChatMessageDTO> adminReply(
            @PathVariable String username,
            @RequestBody ChatMessageDTO dto) {
        ChatMessageDTO saved = chatService.saveAdminReply(username, dto);
        return ResponseEntity.ok(saved);
    }

    /**
     * Admin: view a specific user's thread.
     * GET /api/v1/chat/admin/messages/{username}
     */
    @GetMapping("/admin/messages/{username}")
    public ResponseEntity<List<ChatMessageDTO>> getUserMessages(
            @PathVariable String username) {
        return ResponseEntity.ok(chatService.getConversation(username));
    }

    /**
     * Admin: list all usernames with open chat threads.
     * GET /api/v1/chat/admin/users
     */
    @GetMapping("/admin/users")
    public ResponseEntity<List<String>> getChatUsers() {
        return ResponseEntity.ok(chatService.getAllChatUsernames());
    }
}
