package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.GroupChatDto;
import com.datingapp.datingapp.entity.HistoryRequest;
import com.datingapp.datingapp.entity.MessageDTO;
import com.datingapp.datingapp.services.ChatService;
import com.datingapp.datingapp.services.MessageService;
import com.datingapp.datingapp.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final NotificationController notificationController;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    @Autowired
    public MessageController(MessageService messageService, NotificationController notificationController,
                             SimpMessagingTemplate simpMessagingTemplate, ChatService chatService) {
        this.notificationController = notificationController;
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
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
        int chat_id = mess.getPkChat();
        try {
            simpMessagingTemplate.convertAndSend(
                    "/topic/messages/" + chat_id,
                    messageService.saveMessage(mess));

            GroupChatDto groupChatDto = chatService.getChat(chat_id);
            simpMessagingTemplate.convertAndSend(
                    "/topic/group_chats/" + chat_id + "/updated" ,
                    groupChatDto
            );
            //return messageService.saveMessage(mess);
        } catch (Exception e) {
            e.printStackTrace();
            //return null;rf
        }
    }

    /*@MessageMapping("/test")
    @SendTo("/topic/messages")
    public void sendMessage(@Payload String str) {

        log.info("Стринг Вани " + str);
    }*/

    @MessageMapping("/history/{chatId}")
    @SendTo(value = "/topic/history/{chatId}") //
    public List<MessageDTO> getChatHistory(@DestinationVariable int chatId,
                                           @Payload HistoryRequest req) { //
        //log.info("История отправлена " + messageService.getChatHistory(chatId).toString());
        return messageService.getChatHistory(chatId, req.getLimit(), req.getOffset());
    }
}
