package com.example.datingappclient.retrofit.repository;

import android.util.Log;

import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.ChatsAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsRepository {
    private final ChatsAPI chatsAPI;

    public ChatsRepository() {
        chatsAPI = RetrofitClient.getClient().create(ChatsAPI.class);
    }

    /* === Interfaces === */
    public interface ChatsCallback {
        void onSuccess(List<Object[]> chats);
        void onEmpty(String message);
        void onError(String errorMessage);
    }

    public interface CreateChatCallback {
        void onSuccess(Integer chatId);
        void onExists(String message);
        void onError(String errorMessage);
    }

    /* === Methods === */
    public void fetchUserChats(int userId, ChatsCallback callback) {
        chatsAPI.getChats(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                if (!response.isSuccessful()) callback.onError("Ошибка получения чатов: " + response.message());
                if (response.body() != null) {
                    callback.onSuccess(response.body());
                }
                else {
                    callback.onEmpty("Чаты отсутствуют: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Object[]>> call, Throwable throwable) {
                Log.e("ChatsRepository", "Ошибка сети или ошибка при обработке данных" + call.toString(), throwable);
                callback.onError("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage());
            }
        });
    }

    public void createChat(int userId, int likerId, CreateChatCallback callback) {
        chatsAPI.createChat(userId, likerId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.code() == 409) callback.onExists("Чат уже существует!");
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                }
                else {
                    callback.onError("Ошибка при создании чата: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                Log.e("ChatsRepository", "Ошибка сети или ошибка при обработке данных" + call.toString(), throwable);
                callback.onError("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage());
            }
        });
    }
}
