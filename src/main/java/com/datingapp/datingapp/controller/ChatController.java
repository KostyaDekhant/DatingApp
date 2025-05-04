package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/{userId}") //users
    public ResponseEntity<List<Object[]>> findChatUsers(@PathVariable int userId) {
        return ResponseEntity.ok(chatService.findChatUsers(userId));
    }

    @PostMapping
    public ResponseEntity<Integer> createChat(
            @RequestParam int pk_user,
            @RequestParam int pk_user1) {
        int chatId = chatService.createChat(pk_user, pk_user1);
        return ResponseEntity.ok(chatId);
    }
}
