package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.services.ChatService;
import com.datingapp.datingapp.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    @GetMapping("/chats/{userId}") //users
    public ResponseEntity<List<ChatDTO>> findChatUsers(@PathVariable int userId) {
        return ResponseEntity.ok(chatService.findChatUsers(userId));
    }

    @GetMapping("/group_chats/{chatId}/users/{userId}") //users
    public ResponseEntity<List<ChatMemberDTO>> findGroupChatUsers(@PathVariable int userId, @PathVariable int chatId) {
        return ResponseEntity.ok(chatService.findGroupChatUsers(userId, chatId));
    }

    @GetMapping("/chats/{chatId}/users/{userId}") //users
    public ResponseEntity<ChatDTO> findChatUsers(@PathVariable int userId, @PathVariable int chatId) {
        return ResponseEntity.ok(chatService.getChatById(userId, chatId));
    }

    @PostMapping("/chats")
    public ResponseEntity<Integer> createChat(
            @RequestParam int userA,
            @RequestParam int userB) {
        int chatId = chatService.createChat(userA, userB);
        return ResponseEntity.ok(chatId);
    }

    @GetMapping("/group_chats/{chatId}/info")
    public ResponseEntity<GroupChatInfoDto> getChatInfo(@PathVariable int chatId) {
        GroupChatInfoDto groupChatInfoDto = chatService.getChatInfo(chatId);
        return ResponseEntity.ok(groupChatInfoDto);
    }

    @GetMapping("/group_chats/users/{userId}")
    public ResponseEntity< List<GroupChatDto>> getChats(@PathVariable int userId) {
        List<GroupChatDto> groupChatDtos = chatService.getChats(userId);
        return ResponseEntity.ok(groupChatDtos);
    }

    @PostMapping("/group_chats")
    public ResponseEntity<Integer> addChat(@RequestBody GroupChatDto groupChatDto) {
        Integer id = chatService.saveGroupChat(groupChatDto);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/group_chats/{chatId}/users/{userId}")
    public ResponseEntity<Void> addMemberToChat(@PathVariable int chatId, @PathVariable int userId) {
        chatService.addMember(chatId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/group_chats/{chatId}/users/{userId}/creator/{creatorID}")
    public ResponseEntity<Void> removeMemberFromChat(@PathVariable int chatId, @PathVariable int userId, @PathVariable int creatorId) throws UserNotExistsExceptions {
        chatService.deleteChatMember(chatId, userId, creatorId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/group_chats/{chatId}/creator/{creatorID}")
    public ResponseEntity<Void> removeChat(@PathVariable int chatId, @PathVariable int creatorId) {
        chatService.deleteChat(chatId, creatorId);
        return ResponseEntity.ok().build();
    }
}
