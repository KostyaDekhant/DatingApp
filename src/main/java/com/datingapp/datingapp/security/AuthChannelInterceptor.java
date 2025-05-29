package com.datingapp.datingapp.security;

import com.datingapp.datingapp.controller.UserController;
import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private static final Logger log = LoggerFactory.getLogger(AuthChannelInterceptor.class);
    private final UserRepo userRepo;

    @Autowired
    public AuthChannelInterceptor(JwtUtil jwtUtil,
                                  UserDetailsService userDetailsService, UserRepo userRepo) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepo = userRepo;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() != null) {
            String sessionId = accessor.getSessionId();
            StompCommand command = accessor.getCommand();

            log.info("WebSocket {} — sessionId={}", command, sessionId);

            if (command == StompCommand.CONNECT) {
                String token = accessor.getFirstNativeHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    try {
                        String jwt = token.substring(7);
                        Authentication auth = validate(jwt);
                        accessor.setUser(auth);
                        User user = userRepo.getUserByLogin(jwtUtil.extractUsername(token));
                        log.info("login: " + user.getLogin());
                        user.setIsOnline(true);
                        userRepo.save(user);
                        log.info("WebSocket CONNECT authenticated as '{}'", auth.getName());
                    } catch (Exception e) {
                        log.warn("WebSocket CONNECT failed to authenticate: {}", e.getMessage());
                        throw e;
                    }
                } else {
                    log.warn("WebSocket CONNECT without valid Authorization header");
                }
            }

            if (command == StompCommand.DISCONNECT) {
                log.info("WebSocket DISCONNECT — sessionId={}", sessionId);
            }

            if (command == StompCommand.SUBSCRIBE) {
                String destination = accessor.getDestination();
                log.info("WebSocket SUBSCRIBE to {} — sessionId={}", destination, sessionId);
            }

            if (command == StompCommand.SEND) {
                String destination = accessor.getDestination();
                log.info("WebSocket SEND to {} — sessionId={}", destination, sessionId);
            }
        }

        return message;
    }

    private Authentication validate(String jwt) {
        if (!jwtUtil.validateToken(jwt)) {
            throw new BadCredentialsException("Invalid JWT token");
        }

        String username = jwtUtil.extractUsername(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }
}

