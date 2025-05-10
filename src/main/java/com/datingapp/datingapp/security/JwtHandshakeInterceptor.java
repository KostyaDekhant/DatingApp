// JwtHandshakeInterceptor.java
package com.datingapp.datingapp.security;

import com.datingapp.datingapp.controller.MessageController;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    public JwtHandshakeInterceptor(JwtUtil jwtUtil, UserDetailsService uds) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = uds;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        List<String> auth = request.getHeaders().get("Authorization");
        if (auth == null || auth.isEmpty() || !auth.get(0).startsWith("Bearer ")) {
            return false;
        }
        log.debug(auth.toString());
        log.debug("attrs: "+attributes);
        String token = auth.get(0).substring(7);
        try {
            if (!jwtUtil.validateToken(token)) {
                return false;
            }
            String username = jwtUtil.extractUsername(token);
            UserDetails ud = userDetailsService.loadUserByUsername(username);

            // создаём Principal — можно положить сам UserDetails или UsernamePasswordAuthenticationToken
            UsernamePasswordAuthenticationToken principal =
                    new UsernamePasswordAuthenticationToken(
                            ud, null, ud.getAuthorities());

            attributes.put("principal", principal);
            return true;

        } catch (JwtException ex) {
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception ex
    ) {
        // ничего тут не нужно
    }
}
