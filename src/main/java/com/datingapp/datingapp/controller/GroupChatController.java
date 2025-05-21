package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.ChatMemberDTO;
import com.datingapp.datingapp.entity.GroupChat;
import com.datingapp.datingapp.entity.GroupChatDto;
import com.datingapp.datingapp.entity.GroupChatPayloadInfo;
import com.datingapp.datingapp.exception.ChatNotFoundException;
import com.datingapp.datingapp.services.ChatService;
import com.datingapp.datingapp.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class GroupChatController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    //private final MessageService messageService;
    private final NotificationController notificationController;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    @Autowired
    public GroupChatController(MessageService messageService, NotificationController notificationController,
                             SimpMessagingTemplate simpMessagingTemplate, ChatService chatService) {
        this.notificationController = notificationController;
        //this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
    }

    @MessageMapping("/group_chats/create")
    public int addChat(@RequestBody GroupChatDto groupChatDto) {
        log.info("Добавление чата");
        GroupChat groupChat = chatService.saveGroupChat(groupChatDto);
        int chatId = groupChat.getPkGroupChat();
        for(ChatMemberDTO chatMemberDTO : groupChatDto.getGroupChatInfoDto().getMembers()){
            simpMessagingTemplate.convertAndSend(
                    "/topic/group_chats/" + chatId + "/created",
                    groupChatDto
            );
        }
        Integer id = groupChat.getPkGroupChat();
        return id;
    }

    @MessageMapping("/group_chats/update")
    public void updateGroupChat(@RequestBody GroupChatPayloadInfo info) {
        try {
            int chatId = info.getChatId();
            int userId = info.getUserId();
            String name = info.getName();
            byte[] image = info.getImage();

            List<ChatMemberDTO> chatMemberDTOS = chatService.getChatInfo(chatId).getMembers();
            chatService.updateGroupChat(chatId, userId, name, image);

            GroupChatDto groupChatDto = chatService.getChat(chatId);

            for (ChatMemberDTO chatMemberDTO : chatMemberDTOS) {
                simpMessagingTemplate.convertAndSend(
                        "/topic/group_chats/" + chatId + "/updated",
                        groupChatDto
                );
            }

            //return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при обновлении чата: " + e.getMessage());
        }
    }

    @MessageMapping("/group_chats/remove")
    public void removeChat(@RequestBody GroupChatPayloadInfo info){
        try {
            int chatId = info.getChatId();
            int creatorId = info.getUserId();
            List<ChatMemberDTO> chatMemberDTOS = chatService.getChatInfo(chatId).getMembers();
            chatService.deleteChat(chatId, creatorId);

            for (ChatMemberDTO chatMemberDTO : chatMemberDTOS) {
                simpMessagingTemplate.convertAndSend(
                        "/topic/group_chats/" + chatId + "/deleted",
                        chatId
                );
                log.info("Отправлена информация о удалении чата для пользователя с id: " + chatMemberDTO.getUserId());
            }
            //return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при удалении чата: " + e.getMessage());
        }
    }

    @MessageMapping("/group_chats/test")
    public void test(){
        log.info("Да, оно пришло");
    }

}
