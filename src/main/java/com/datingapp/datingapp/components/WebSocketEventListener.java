package com.datingapp.datingapp.components;

import com.datingapp.datingapp.controller.MessageController;
import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.repository.UserRepo;
import com.datingapp.datingapp.security.AuthChannelInterceptor;
import com.datingapp.datingapp.services.OnlineStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Optional;

@Component
public class WebSocketEventListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);
    @Autowired
    private UserRepo userRepository;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws UserNotExistsExceptions {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        log.info("User " + user.getName() + " disconnected");
        if (user != null) {
            String username = user.getName();
            Optional<User> entity = userRepository.findByLogin(username);
            //log.info("User " + entity);
            if (entity.isPresent()) {
                User userEntity = entity.get();
                userEntity.setIsOnline(false);
                log.info("user " + userEntity.getName() + " is offline");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                userEntity.setLastOnline(timestamp);
                userRepository.save(userEntity);
                OnlineStatusService.sendOnlineStatus(userEntity.getPkUser(), false, timestamp);
            }
        }
    }
}