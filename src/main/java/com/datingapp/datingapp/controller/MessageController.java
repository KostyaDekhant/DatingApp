package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.services.ChatService;
import com.datingapp.datingapp.services.MessageService;
import com.datingapp.datingapp.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.security.Principal;

import java.util.List;

@Controller
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final NotificationController notificationController;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public MessageController(MessageService messageService, NotificationController notificationController,
                             SimpMessagingTemplate simpMessagingTemplate, ChatService chatService,
                             UserService userService) {
        this.notificationController = notificationController;
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
    }

    // Эндпоинт для отправки тестового сообщения всем клиентам
    @GetMapping("/broadcast")
    public String broadcast() {
        String message = "Это тестовое сообщение для всех!";
        notificationController.broadcastMessage(message);
        return "Сообщение отправлено";
    }


    @MessageMapping("/send")
    //@SendToUser("/topic/messages/{chat_id}")
    public void sendMessage(@RequestBody MessageDTO messageDTO) { //MessageDTO
        log.info(messageDTO.toString());
        Message mess = new Message(messageDTO);
        int userId = messageDTO.getPkUser();
        int chat_id = mess.getPkChat();
        try {
            simpMessagingTemplate.convertAndSend(
                    "/topic/messages/" + chat_id,
                    messageService.saveMessage(mess));

            GroupChatDto groupChatDto = chatService.getChat(chat_id, userId);

            for(ChatMemberDTO chatMember : groupChatDto.getGroupChatInfoDto().getMembers()){
                String login = userService.getLoginByPkUser(chatMember.getUserId());
                simpMessagingTemplate.convertAndSend(
                        "/topic/group_chats/"+userId+"/updated",
                        groupChatDto
                );
            }

            //return messageService.saveMessage(mess);
        } catch (Exception e) {
            e.printStackTrace();
            //return null;rf
        } catch (UserNotExistsExceptions e) {
            throw new RuntimeException(e);
        }
    }

    /*@MessageMapping("/test")
    @SendTo("/topic/messages")
    public void sendMessage(@Payload String str) {

        log.info("Стринг Вани " + str);
    }*/

    @MessageMapping("/history/{chatId}")
   //@SendToUser(value = "/topic/history/{chatId}") //
    public List<MessageDTO> getChatHistory(@DestinationVariable int chatId,
                               @Payload HistoryRequest req) throws UserNotExistsExceptions { //

        List<MessageDTO> history = messageService.getChatHistory(chatId, req.getLimit(), req.getOffset());
        log.info("id " + req.getUserId());
        simpMessagingTemplate.convertAndSend(
                "/topic/"+req.getUserId()+"/history/" + chatId, // Используем /queue/ для приватных сообщений
                history
        ); //ToUser
        log.info("История отправлена " +history);
        return history;
    }
}
