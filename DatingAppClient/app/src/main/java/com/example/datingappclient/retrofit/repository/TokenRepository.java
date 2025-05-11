package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.TokenAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenRepository {
    private final TokenAPI tokenAPI;

    public TokenRepository(Context context) {
        tokenAPI = RetrofitClient.getClient(context).create(TokenAPI.class);
    }

    public void tokenIsValid(ResultCallback<Boolean> callback) {
        tokenAPI.tokenIsValid().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onResult(Result.success(true));
                else if (response.code() == 409) callback.onResult(Result.success(false));
                else callback.onResult(Result.error("Ошибка при проверке токена!" + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }
}
