package com.datingapp.datingapp.services;

import com.datingapp.datingapp.controller.ImageController;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;

@Service
public class OnlineStatusService {
    private static final Logger log = LoggerFactory.getLogger(OnlineStatusService.class);
    private static SimpMessagingTemplate simpMessagingTemplate;
    private static UserService userService;

    public OnlineStatusService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    public static void sendOnlineStatus(Integer userId, boolean isOnline, Timestamp timestamp) {
        log.info("Статус пользователя с id " + userId + ": " + (isOnline ? "Online" : "Offline"));

        simpMessagingTemplate.convertAndSend("/topic/online", new OnlineStatus(userId, isOnline, timestamp));
    }

    public record OnlineStatus(Integer id, Boolean isOnline, Timestamp timestamp) {}
}