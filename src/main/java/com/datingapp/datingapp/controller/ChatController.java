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


    @GetMapping("/group_chats/avatars")
    public ResponseEntity< List<GroupChatDto>> getChatAvatars(@RequestParam("userId") int userId,
                                                              @RequestParam("chatIds") List<Integer> chatIds) {
        List<GroupChatDto> groupChatDtos = chatService.getChatAvatars(userId, chatIds);
        return ResponseEntity.ok(groupChatDtos);
    }

    //@PostMapping("/group_chats")
    @MessageMapping("/group_chats/create")
    public ResponseEntity<Integer> addChat(@RequestBody GroupChatDto groupChatDto) {
        GroupChat groupChat = chatService.saveGroupChat(groupChatDto);
        for(ChatMemberDTO chatMemberDTO : groupChatDto.getGroupChatInfoDto().getMembers()){
            simpMessagingTemplate.convertAndSendToUser(
                    chatMemberDTO.getUserId().toString(),
                    "/queue/group_chats/update",
                    groupChatDto
            );
        }
        Integer id = groupChat.getPkGroupChat();
        return ResponseEntity.ok(id);
    }

    @PostMapping("/group_chats/{chatId}/users/{userId}")
    public ResponseEntity<Void> addMemberToChat(@PathVariable int chatId, @PathVariable int userId) {
        chatService.addMember(chatId, userId);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/group_chats/update")
    public ResponseEntity<Void> updateGroupChat(@RequestBody GroupChatPayloadInfo info) {
        try {
            int chatId = info.getChatId();
            int userId = info.getUserId();
            String name = info.getName();
            byte[] image = info.getImage();

            List<ChatMemberDTO> chatMemberDTOS = chatService.getChatInfo(chatId).getMembers();
            chatService.updateGroupChat(chatId, userId, name, image);

            GroupChatDto groupChatDto = chatService.getChat(chatId);

            for (ChatMemberDTO chatMemberDTO : chatMemberDTOS) {
                simpMessagingTemplate.convertAndSendToUser(
                        chatMemberDTO.getUserId().toString(),
                        "/queue/group_chats/updated",
                        groupChatDto
                );
            }

            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при обновлении чата: " + e.getMessage());
        }
    }

    @DeleteMapping("/group_chats/{chatId}/users/{userId}/creator/{creatorId}")
    public ResponseEntity<Void> removeMemberFromChat(@PathVariable int chatId, @PathVariable int userId, @PathVariable int creatorId) throws UserNotExistsExceptions {
        chatService.deleteChatMember(chatId, userId, creatorId);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/group_chats/remove")
    public ResponseEntity<Void> removeChat(@RequestBody GroupChatPayloadInfo info){
        try {
            int chatId = info.getChatId();
            int creatorId = info.getUserId();
            List<ChatMemberDTO> chatMemberDTOS = chatService.getChatInfo(chatId).getMembers();
            chatService.deleteChat(chatId, creatorId);

            for (ChatMemberDTO chatMemberDTO : chatMemberDTOS) {
                simpMessagingTemplate.convertAndSendToUser(
                        chatMemberDTO.getUserId().toString(),
                        "/queue/group_chats/deleted",
                        chatId
                );
            }
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при удалении чата: " + e.getMessage());
        }
    }
}
