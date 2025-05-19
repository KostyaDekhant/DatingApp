package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.ChatsAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsRepository {
    private final ChatsAPI chatsAPI;

    public ChatsRepository(Context context) {
        chatsAPI = RetrofitClient.getClient(context).create(ChatsAPI.class);
    }


    /* === Group Chats === */
    public void fetchChatInfo(int chatId, ResultCallback<ChatInfoDTO> callback) {
        chatsAPI.getChatInfo(chatId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ChatInfoDTO> call, Response<ChatInfoDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        callback.onResult(Result.success(response.body()));
                    else
                        callback.onResult(Result.empty());
                }
                else {
                    callback.onResult(Result.error("Ошибка при получении информации о чате! " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ChatInfoDTO> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchUserChats(int userId, int limit, int offset, ResultCallback<List<ChatDTO>> callback) {
        chatsAPI.getChats(userId, limit, offset).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<ChatDTO>> call, Response<List<ChatDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty())
                        callback.onResult(Result.success(response.body()));
                    else
                        callback.onResult(Result.empty());
                } else {
                    callback.onResult(Result.error("Ошибка при получении групповых чатов пользователя: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<ChatDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchChatAvatar(int userId, int chatId, ResultCallback<List<ChatDTO>> callback) {
        List<Integer> ids = new ArrayList<>();
        ids.add(chatId);
        chatsAPI.getChatsAvatars(userId, ids).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<ChatDTO>> call, Response<List<ChatDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null || response.body().isEmpty() || response.body().get(0).getImage() == null) callback.onResult(Result.empty());
                    else callback.onResult(Result.success(response.body()));
                } else
                    callback.onResult(Result.error("Ошибка при получении изображений чата " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<List<ChatDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchChatMembers(int userId, int chatId, ResultCallback<List<ChatMemberDTO>> callback) {
        chatsAPI.getChatMembers(userId, chatId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<ChatMemberDTO>> call, Response<List<ChatMemberDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                } else {
                    callback.onResult(Result.error("Не удалось получить участников чата! " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<ChatMemberDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void createChat(ChatDTO ChatDTO, ResultCallback<Integer> callback) {
        chatsAPI.createGroupChat(ChatDTO).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    callback.onResult(Result.error("Ошибка при создании группового чата: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void addMemberToChat(int chatId, int userId, ResultCallback<Void> callback) {
        chatsAPI.addMemberToChat(chatId, userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error("Ошибка при добавлении пользователя в чат: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void deleteChat(int chatId, int creatorId, ResultCallback<Void> callback) {
        chatsAPI.deleteChat(chatId, creatorId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onResult(Result.success(null));
                else callback.onResult(Result.error("Ошибка при удалении чата:"  + chatId + " " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }
}
