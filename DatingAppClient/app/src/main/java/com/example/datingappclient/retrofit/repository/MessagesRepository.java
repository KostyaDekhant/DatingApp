package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.controllers.MessagesController;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesRepository {

    private final MessagesController messagesController;

    public MessagesRepository(Context context) {
        messagesController = RetrofitClient.getClient(context).create(MessagesController.class);
    }

    public void fetchPersonalUnreadMessages(int chatId, int userId, ResultCallback<List<MessageDTO>> callback) {
        messagesController.getPersonalUnreadMessages(chatId, userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<MessageDTO>> call, Response<List<MessageDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                }
                else callback.onResult(Result.error("Ошибка при получении непрочитанных собщений! " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<List<MessageDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или данных!" + throwable.getMessage()));
            }
        });
    }

    public void fetchChatUnreadMessages(int chatId, int userId, ResultCallback<List<MessageDTO>> callback) {
        messagesController.getChatUnreadMessages(chatId, userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<MessageDTO>> call, Response<List<MessageDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                }
                else callback.onResult(Result.error("Ошибка при получении непрочитанных собщений! " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<List<MessageDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или данных!" + throwable.getMessage()));
            }
        });
    }

    public void fetchHistory(int chatId, int limit, int offset, ResultCallback<List<MessageDTO>> callback) {
        messagesController.getHistory(chatId, limit, offset).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<MessageDTO>> call, Response<List<MessageDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                }
                else callback.onResult(Result.error("Ошибка при получении истории собщений! " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<List<MessageDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или данных!" + throwable.getMessage()));
            }
        });
    }
}
