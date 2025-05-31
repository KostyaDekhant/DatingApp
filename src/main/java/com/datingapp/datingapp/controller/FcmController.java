package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.repository.UserRepo;
import com.datingapp.datingapp.services.FcmService;
import com.datingapp.datingapp.services.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fcm")
public class FcmController {

    private final FcmService fcmService;
    private static final Logger log = LoggerFactory.getLogger(FcmController.class);

    public FcmController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerToken(@RequestParam int userId, @RequestParam String token) {
        log.info("Регистрация FCM токена для пользователя с id " + userId);
        fcmService.registerToken(userId, token);
        return ResponseEntity.ok().build();
    }
}