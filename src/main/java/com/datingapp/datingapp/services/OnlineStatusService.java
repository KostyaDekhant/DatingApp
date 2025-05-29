package com.datingapp.datingapp.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class OnlineStatusService {

    private static SimpMessagingTemplate simpMessagingTemplate;

    public OnlineStatusService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    public static void sendOnlineStatus(Integer userId, boolean isOnline) {
        simpMessagingTemplate.convertAndSend("/topic/online", new OnlineStatus(userId, isOnline));
    }

    public record OnlineStatus(Integer id, Boolean isOnline) {}
}