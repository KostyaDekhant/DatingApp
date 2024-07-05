package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.enitity.ChatMessageDto;
import com.datingapp.datingapp.services.MessageService;
import com.datingapp.datingapp.enitity.Message;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Controller
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final NotificationController notificationController;

    @Autowired
    public MessageController(MessageService messageService, NotificationController notificationController) {
        this.notificationController = notificationController;
        this.messageService = messageService;
    }

    // Эндпоинт для отправки тестового сообщения всем клиентам
    @GetMapping("/broadcast")
    public String broadcast() {
        String message = "Это тестовое сообщение для всех!";
        notificationController.broadcastMessage(message);
        return "Сообщение отправлено";
    }


    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Message sendMessage(@Payload Message stompMessage) {

        log.info(broadcast());
        try {
            return messageService.saveMessage(stompMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @MessageMapping("/test")
    @SendTo("/topic/messages")
    public void sendMessage(@Payload String str) {

        log.info("Стринг Вани" + str);
    }

    @MessageMapping("/history/{chatId}")
    @SendTo("/topic/history")
    public List<Message> getChatHistory(@DestinationVariable int chatId) { //
        log.info(messageService.getChatHistory(chatId).toString());
        return messageService.getChatHistory(chatId);
    }




}
