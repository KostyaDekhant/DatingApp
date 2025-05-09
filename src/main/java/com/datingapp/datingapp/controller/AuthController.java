package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.security.JwtUtil;
import com.datingapp.datingapp.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public AuthController(
            AuthenticationManager authManager,
            JwtUtil jwtUtil,
            UserService userService
    ) {
        this.authManager = authManager;
        this.jwtUtil     = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String login    = user.getLogin();
            String password = user.getPassword();
            // Spring Security проверит логин/пароль через UserDetailsService
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password)
            );

            // Если нет исключения — выдаём токен
            String token = jwtUtil.generateToken(login);
            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException ex) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "Неверный логин или пароль"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        Integer userId = userService.signupUser(user);
        String token = jwtUtil.generateToken(user.getLogin());
        return ResponseEntity.ok(Map.of("id", userId, "token", token));
    }
}
