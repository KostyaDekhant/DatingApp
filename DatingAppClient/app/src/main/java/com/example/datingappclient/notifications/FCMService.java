package com.example.datingappclient.notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.retrofit.repository.FCMRepository;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class FCMService extends FirebaseMessagingService {

    private FCMRepository fcmRepository;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Получен новый токен: " + token);

        // Отправка токена на сервер
        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        int chatId = 0;
        // Проверка на наличие data payload
        if (!remoteMessage.getData().isEmpty()) {
            Map<String, String> data = remoteMessage.getData();

            // Получение chatId
            String chatIdStr = data.get("chatId");

            assert chatIdStr != null;
            chatId = Integer.parseInt(chatIdStr);

            if (chatIdStr != null) {
                Log.d("FCM", "Получен chatId: " + chatIdStr);
                // можно, например, открыть нужный чат или сохранить chatId
            }
        }

        // 1. Уведомление (если отправлено через `notification`)
        if (remoteMessage.getNotification() != null) {
            if (chatId == 0 || ChatDTO.selectedChat != null && ChatDTO.selectedChat.getId() == chatId) return;
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            //String imageUrl = remoteMessage.getNotification().getImageUrl().toString();
            Log.d("FCM", "Title: " + title + "\nBody: " + body);
            showNotification(title, body, getImage("imageUrl"));
        }
    }

    private Bitmap getImage(String imageUrl) {
        try {
            // Загружаем картинку
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        }
        catch (Exception e) {
            Log.e("FCM", e.getMessage());
        }
        return null;
    }

    private void showNotification(String title, String body, Bitmap imageUrl) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "default_channel_id";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Уведомления", NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);


        notificationManager.notify(1, builder.build());
    }

    public void sendTokenToServer(String token) {
        fcmRepository = new FCMRepository(DatingAppApplication.getInstance().getApplicationContext());

        String logTag = Constants.GLOBAL_LOG_TAG + "FCM";
        int userId = DatingAppApplication.getTokenManager().getUserId();
        fcmRepository.registerToken(userId, token, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.d(logTag, "Токен успешно зарегестрирован!");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }
}
