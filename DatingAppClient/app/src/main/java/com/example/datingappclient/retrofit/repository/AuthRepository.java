package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.TokenRefreshRequest;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.AuthAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private AuthAPI authAPI;

    public AuthRepository(AuthAPI authAPI) {
        this.authAPI = authAPI;
    }

    public void refreshToken(TokenRefreshRequest tokenRefreshRequest, ResultCallback<AuthResponse> callback) {
        authAPI.refreshToken(tokenRefreshRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    callback.onResult(Result.error("Ошибка обновления токена: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                callback.onResult(Result.error(throwable.getMessage()));
            }
        });
    }

    public AuthResponse refreshTokenSync(TokenRefreshRequest tokenRefreshRequest) throws IOException {
        Response<AuthResponse> response = authAPI.refreshToken(tokenRefreshRequest).execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            throw new IOException("Ошибка обновления токена: " + response.code());
        }
    }

    public void logout(int userId, ResultCallback<Void> callback) {
        authAPI.logout(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onResult(Result.success(null));
                else callback.onResult(Result.error("Ошибка выхода из аккаунта!"));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или передачи данных! " + throwable.getMessage()));
            }
        });
    }

}
