package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate, ChatService chatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
    }


    @GetMapping("/group_chats/{chatId}/users/{userId}") //users
    public ResponseEntity<List<ChatMemberDTO>> findGroupChatUsers(@PathVariable int userId, @PathVariable int chatId) {
        return ResponseEntity.ok(chatService.findGroupChatUsers(userId, chatId));
    }

    @GetMapping("/group_chats/{chatId}/info")
    public ResponseEntity<GroupChatInfoDto> getChatInfo(@PathVariable("chatId") int chatId) {
        GroupChatInfoDto groupChatInfoDto = chatService.getChatInfo(chatId);
        return ResponseEntity.ok(groupChatInfoDto);
    }

    @GetMapping("/users/{userId}/group_chats")
    public ResponseEntity< List<GroupChatDto>> getChats(@PathVariable("userId") int userId,
                                                        @RequestParam("limit") int limit,
                                                        @RequestParam("offset") int offset) {
        List<GroupChatDto> groupChatDtos = chatService.getChats(userId, limit, offset);
        return ResponseEntity.ok(groupChatDtos);
    }

    @GetMapping("/group_chats/{chatId}")
    public ResponseEntity<GroupChatDto> getChat(@PathVariable("chatId") int chatId, @RequestParam("userId") int userId) {
        GroupChatDto groupChatDto = chatService.getChat(chatId, userId);
        return ResponseEntity.ok(groupChatDto);
    }

    @PatchMapping("/group_chats/{chatId}")
    public ResponseEntity<Void> updateChat(@PathVariable("chatId") int chatId,
                                           @RequestBody GroupChatPayloadInfo payloadInfo) {
        log.info("Обновление чата: {}", payloadInfo.toString());
        chatService.updateGroupChat(payloadInfo.getChatId(), payloadInfo.getUserId(), payloadInfo.getName(), payloadInfo.getImage());
        broadcastUpdateChatEvent(payloadInfo.getChatId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/group_chats/create")
    public int addChat(@RequestBody GroupChatDto groupChatDto) {
        log.info("Добавление чата");
        GroupChat groupChat = chatService.saveGroupChat(groupChatDto);
        int chatId = groupChat.getPkGroupChat();
        groupChatDto.setPkGroupChat(chatId);
        for(ChatMemberDTO chatMember : groupChatDto.getGroupChatInfoDto().getMembers()){
            broadcastCreateChatEvent(chatMember.getUserId(), chatId);
        }
        return chatId;
    }

    public void broadcastCreateChatEvent(int userId, int chatId) {
        simpMessagingTemplate.convertAndSend(
                "/topic/group_chats/"+userId+"/created",
                chatId
        );
    }

    public void broadcastUpdateChatEvent(int chatId) {
        simpMessagingTemplate.convertAndSend(
                "/topic/group_chats/"+chatId+"/updated",
                true
        );
    }


    @GetMapping("/group_chats/{chatId}/avatar")
    public ResponseEntity<GroupChatDto> getChatAvatar(@RequestParam("userId") int userId,
                                                              @PathVariable("chatId") int chatId) {
        GroupChatDto groupChatDtos = chatService.getChatAvatars(userId, chatId);
        return ResponseEntity.ok(groupChatDtos);
    }

    @PostMapping("/group_chats/{chatId}/members")
    public ResponseEntity<Void> addMembersToChat(@RequestParam("chatId") int chatId, @RequestParam("userIds") List<Integer> userIds) {
        chatService.addMembers(chatId, userIds);
        broadcastUpdateChatEvent(chatId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/group_chats/{chatId}/members/{userId}")
    public ResponseEntity<Void> removeMemberFromChat(@PathVariable int chatId, @PathVariable int userId, @RequestParam("creatorId") int creatorId) throws UserNotExistsExceptions {
        chatService.deleteChatMember(chatId, userId, creatorId);
        broadcastUpdateChatEvent(chatId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/group_chats/{chatId}/users/{userId}/messages/unread")
    public ResponseEntity<List<MessageDTO>> getUnreadMessages(@PathVariable int chatId, @PathVariable("userId") int userId) {
        return ResponseEntity.ok(chatService.getUnreadMessages(chatId, userId));
    }

    @GetMapping("/group_chats/{chatId}/messages/unread")
    public ResponseEntity<List<MessageDTO>> getAnotherUnreadMessages(@PathVariable int chatId, @RequestParam("userId") int userId) {
        return ResponseEntity.ok(chatService.getAnotherUnreadMessages(chatId, userId));
    }
}
