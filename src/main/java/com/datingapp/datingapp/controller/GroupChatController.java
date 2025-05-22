package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.ChatNotFoundException;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.services.ChatService;
import com.datingapp.datingapp.services.MessageService;
import com.datingapp.datingapp.services.UserService;
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
    private final UserService userService;

    @Autowired
    public GroupChatController(MessageService messageService, NotificationController notificationController,
                               SimpMessagingTemplate simpMessagingTemplate, ChatService chatService, UserService userService) {
        this.notificationController = notificationController;
        //this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
    }

    @MessageMapping("/group_chats/create")
    public int addChat(@RequestBody GroupChatDto groupChatDto) throws UserNotExistsExceptions {
        log.info("Добавление чата");
        GroupChat groupChat = chatService.saveGroupChat(groupChatDto);
        int chatId = groupChat.getPkGroupChat();
        for(ChatMemberDTO chatMember : groupChatDto.getGroupChatInfoDto().getMembers()){
            String login = userService.getLoginByPkUser(chatMember.getUserId());
            simpMessagingTemplate.convertAndSendToUser(
                    login,
                    "/topic/group_chats/created",
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
            byte[] image = null;
            if(info.getImage() != null)
                image = info.getImage();
            List<ChatMemberDTO> chatMemberDTOS = chatService.getChatInfo(chatId).getMembers();
            chatService.updateGroupChat(chatId, userId, name, image);

            GroupChatDto groupChatDto = chatService.getChat(chatId,userId);

            for(ChatMemberDTO chatMember : groupChatDto.getGroupChatInfoDto().getMembers()){
                String login = userService.getLoginByPkUser(chatMember.getUserId());
                simpMessagingTemplate.convertAndSendToUser(
                        login,
                        "/topic/group_chats/updated",
                        groupChatDto
                );
            }

            //return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при обновлении чата: " + e.getMessage());
        } catch (UserNotExistsExceptions e) {
            throw new RuntimeException(e);
        }
    }

    @MessageMapping("/group_chats/remove")
    public void removeChat(@RequestBody GroupChatPayloadInfo info){
        try {
            int chatId = info.getChatId();
            int creatorId = info.getUserId();
            List<ChatMemberDTO> chatMemberDTOS = chatService.getChatInfo(chatId).getMembers();


            GroupChatDto groupChatDto = chatService.getChat(chatId, creatorId);
            chatService.deleteChat(chatId, creatorId);

            for(ChatMemberDTO chatMember : groupChatDto.getGroupChatInfoDto().getMembers()){
                String login = userService.getLoginByPkUser(chatMember.getUserId());
                simpMessagingTemplate.convertAndSendToUser(
                        login,
                        "/topic/group_chats/deleted",
                        groupChatDto
                );
            }
            log.info("Отправлена информация о удалении чата");
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при удалении чата: " + e.getMessage());
        } catch (UserNotExistsExceptions e) {
            throw new RuntimeException(e);
        }
    }
}
