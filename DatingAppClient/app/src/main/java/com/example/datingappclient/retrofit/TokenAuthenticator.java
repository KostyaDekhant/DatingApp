package com.example.datingappclient.retrofit;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        String logTag = Constants.GLOBAL_LOG_TAG + "TOKEN ERROR";
        Log.e(logTag, "Ошибка входа, пробую обновить токен!" + response.message());

        String refreshToken = tokenManager.getRefreshToken();
        String token = tokenManager.getAccessToken();

        try {
            AuthResponse authResponse = authRepository.refreshTokenSync(new TokenRefreshRequest(refreshToken));
            tokenManager.saveAccessToken(authResponse.getToken());
            tokenManager.saveRefreshToken(authResponse.getRefreshToken());
            tokenManager.saveUserId(authResponse.getUserId());

            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + authResponse.getToken())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int responseCount(Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) count++;
        return count;
    }
}
