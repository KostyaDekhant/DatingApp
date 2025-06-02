package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.controllers.FCMController;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMRepository {

    private FCMController fcmController;

    public FCMRepository(Context context) {
        fcmController = RetrofitClient.getClient(context).create(FCMController.class);
    }

    public void registerToken(int userId, String token, ResultCallback<Void> callback) {
        fcmController.registerToken(userId, token).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onResult(Result.success(null));
                else callback.onResult(Result.error("Ошибка регистрации токена " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка при передаче запроса или получении ответа!" + throwable.getMessage()));
            }
        });
    }

}
