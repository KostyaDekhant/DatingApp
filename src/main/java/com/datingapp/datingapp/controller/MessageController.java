package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.enitity.ChatMessageDto;
import com.datingapp.datingapp.services.MessageService;
import com.datingapp.datingapp.enitity.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Controller
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Message sendMessage(@Payload String stompMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = null;
        try {
            // Парсинг JSON тела STOMP сообщения в объект Message
            message = objectMapper.readValue(stompMessage, Message.class);
            // Сохранение сообщения через сервис
            return messageService.saveMessage(message);
        } catch (IOException e) {
            // Обработка ошибок парсинга
            e.printStackTrace();
            // Возвращение null или создание сообщения об ошибке
            return null;
        }
    }

    @MessageMapping("/history/{chatId}")
    @SendTo("/topic/history")
    public List<ChatMessageDto> getChatHistory(@DestinationVariable int chatId) {
        return messageService.getChatHistory(chatId);
    }



}
