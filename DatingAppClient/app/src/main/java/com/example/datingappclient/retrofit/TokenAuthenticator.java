package com.example.datingappclient.retrofit;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.TokenManager;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.TokenRefreshRequest;
import com.example.datingappclient.retrofit.repository.AuthRepository;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {


    private final AuthRepository authRepository;
    private final TokenManager tokenManager;

    // объект блокировки
    private final Object lock = new Object();

    public TokenAuthenticator(AuthRepository authRepository, TokenManager tokenManager) {
        this.authRepository = authRepository;
        this.tokenManager = tokenManager;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) {
        if (responseCount(response) >= 2) {
            return null;
        }

        synchronized (lock) {
            String newAccessToken = tokenManager.getAccessToken();
            String logTag = Constants.GLOBAL_LOG_TAG + "TOKEN ERROR";
            Log.d(logTag, "Токен не валиден (401), пробую обновить!");
            // Возможно, кто-то уже обновил токен — проверим
            String requestAccessToken = extractAccessToken(response.request());
            if (newAccessToken != null && !newAccessToken.equals(requestAccessToken)) {
                // Кто-то другой уже обновил — просто повторим запрос с новым токеном
                Log.d(logTag, "Токен уже обновился (асинхронный запрос)");
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newAccessToken)
                        .build();
            }

            Log.d(logTag, "Попытка обновления");
            // Иначе — пытаемся обновить токен
            String refreshToken = tokenManager.getRefreshToken();
            if (refreshToken == null || refreshToken.isEmpty()) {
                forceLogout();
                return null;
            }

            try {
                AuthResponse authResponse = authRepository.refreshTokenSync(new TokenRefreshRequest(refreshToken));
                tokenManager.saveAccessToken(authResponse.getToken());
                tokenManager.saveRefreshToken(authResponse.getRefreshToken());
                tokenManager.saveUserId(authResponse.getUserId());

                Log.d(logTag, "Токен обновлен успещно!");

                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + authResponse.getToken())
                        .build();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private int responseCount(Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) count++;
        return count;
    }

    private String extractAccessToken(Request request) {
        String header = request.header("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring("Bearer ".length());
        }
        return null;
    }

    private void forceLogout() {
        TokenManager tokenManager = new TokenManager(DatingAppApplication.getInstance().getApplicationContext());
        tokenManager.clearTokens();

        Intent intent = new Intent("com.example.datingappclient.LOGOUT");
        LocalBroadcastManager.getInstance(DatingAppApplication.getInstance().getApplicationContext()).sendBroadcast(intent);
    }
}
