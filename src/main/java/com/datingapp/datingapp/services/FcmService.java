package com.datingapp.datingapp.services;

import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.repository.UserRepo;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.Notification;
import java.util.Map;

@Service
public class FcmService {

    private final UserRepo userRepo;

    public FcmService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void sendPushNotificationToUser(String fcmToken, String title, String body, Map<String, String> data) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification);

            if (data != null) {
                messageBuilder.putAllData(data);
            }

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerToken(int userId, String token) {
        try {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setFcmToken(token);
            userRepo.save(user);
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении FCM токена: " +e);
        }
    }
}