package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.ChatNotFoundException;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.services.ChatService;
import com.datingapp.datingapp.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate, ChatService chatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
    }


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

    @GetMapping("/group_chats/info")
    public ResponseEntity<GroupChatInfoDto> getChatInfo(@RequestParam("chatId") int chatId) {
        GroupChatInfoDto groupChatInfoDto = chatService.getChatInfo(chatId);
        return ResponseEntity.ok(groupChatInfoDto);
    }

    @GetMapping("/group_chats")
    public ResponseEntity< List<GroupChatDto>> getChats(@RequestParam("userId") int userId,
                                                        @RequestParam("limit") int limit,
                                                        @RequestParam("offset") int offset) {
        List<GroupChatDto> groupChatDtos = chatService.getChats(userId, limit, offset);
        return ResponseEntity.ok(groupChatDtos);
    }

    @PatchMapping("/group_chats")
    public ResponseEntity<Void> updateChat(@RequestBody GroupChatPayloadInfo payloadInfo) {
        log.info("Обновление чата: {}", payloadInfo.toString());
        chatService.updateGroupChat(payloadInfo.getChatId(), payloadInfo.getUserId(), payloadInfo.getName(), payloadInfo.getImage());
        broadcastUpdateChatEvent(payloadInfo.getChatId(), payloadInfo.getUserId());
        return ResponseEntity.ok().build();
    }

    public void broadcastUpdateChatEvent(int chatId, int userId) {
        GroupChatDto groupChatDto = chatService.getChat(chatId,userId);
        simpMessagingTemplate.convertAndSend(
                "/topic/group_chats/"+chatId+"/updated",
                groupChatDto
        );
    }


    @GetMapping("/group_chats/avatars")
    public ResponseEntity< List<GroupChatDto>> getChatAvatars(@RequestParam("userId") int userId,
                                                              @RequestParam("chatIds") List<Integer> chatIds) {
        List<GroupChatDto> groupChatDtos = chatService.getChatAvatars(userId, chatIds);
        return ResponseEntity.ok(groupChatDtos);
    }

    @PostMapping("/group_chats/{chatId}/users/{userId}")
    public ResponseEntity<Void> addMemberToChat(@PathVariable int chatId, @PathVariable int userId) {
        chatService.addMember(chatId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/group_chats/{chatId}/users/{userId}/creator/{creatorId}")
    public ResponseEntity<Void> removeMemberFromChat(@PathVariable int chatId, @PathVariable int userId, @PathVariable int creatorId) throws UserNotExistsExceptions {
        chatService.deleteChatMember(chatId, userId, creatorId);
        return ResponseEntity.ok().build();
    }
}
