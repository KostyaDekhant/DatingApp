package com.example.datingappclient.retrofit.repository;

import android.content.Context;
import android.util.Log;

import com.example.datingappclient.model.ChatDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.ChatsAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsRepository {
    private final ChatsAPI chatsAPI;

    public ChatsRepository(Context context) {
        chatsAPI = RetrofitClient.getClient(context).create(ChatsAPI.class);
    }

    /* === Methods === */
    public void fetchUserChats(int userId, ResultCallback<List<ChatDTO>> callback) {
        chatsAPI.getChats(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<ChatDTO>> call, Response<List<ChatDTO>> response) {
                if (!response.isSuccessful()) callback.onResult(Result.error("Ошибка получения чатов: " + response.message()));
                if (response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                }
                else {
                    callback.onResult(Result.empty());
                }
            }

            @Override
            public void onFailure(Call<List<ChatDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchUserChat(int userId, int chatId, ResultCallback<ChatDTO> callback) {
        chatsAPI.getChat(userId, chatId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ChatDTO> call, Response<ChatDTO> response) {
                if (!response.isSuccessful()) callback.onResult(Result.error("Ошибка получения чата (" + chatId +"): " + response.message()));
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                }
                else {
                    callback.onResult(Result.error("Ошибка при получении чата: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ChatDTO> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void createChat(int userId, int likerId, ResultCallback<Integer> callback) {
        chatsAPI.createChat(userId, likerId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.code() == 409) callback.onResult(Result.exists());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                }
                else {
                    callback.onResult(Result.error("Ошибка при создании чата: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }
}
