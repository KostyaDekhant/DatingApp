package com.datingapp.datingapp.services;

import com.datingapp.datingapp.controller.ImageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class OnlineStatusService {
    private static final Logger log = LoggerFactory.getLogger(OnlineStatusService.class);
    private static SimpMessagingTemplate simpMessagingTemplate;

    public OnlineStatusService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    public static void sendOnlineStatus(Integer userId, boolean isOnline) {
        log.info("Статус пользователя с id " + userId + ": " + (isOnline ? "Online" : "Offline"));
        simpMessagingTemplate.convertAndSend("/topic/online", new OnlineStatus(userId, isOnline));
    }

    public record OnlineStatus(Integer id, Boolean isOnline) {}
}