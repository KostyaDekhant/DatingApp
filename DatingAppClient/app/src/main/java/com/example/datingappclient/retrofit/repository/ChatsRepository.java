package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.ChatPayloadInfo;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.controllers.ChatsController;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsRepository {
    private final ChatsController chatsController;

    public ChatsRepository(Context context) {
        chatsController = RetrofitClient.getClient(context).create(ChatsController.class);
    }


    /* === Group Chats === */
    public void fetchChatInfo(int chatId, ResultCallback<ChatInfoDTO> callback) {
        chatsController.getChatInfo(chatId).enqueue(new Callback<>() {
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
        chatsController.getChats(userId, limit, offset).enqueue(new Callback<>() {
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

    public void fetchChatAvatar(int userId, int chatId, ResultCallback<ChatDTO> callback) {
        chatsController.getChatAvatar(chatId, userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ChatDTO> call, Response<ChatDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null || response.body().getImage() == null) callback.onResult(Result.empty());
                    else callback.onResult(Result.success(response.body()));
                } else
                    callback.onResult(Result.error("Ошибка при получении изображений чата " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<ChatDTO> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchPossibleChatMembers(int userId, int chatId, ResultCallback<List<ChatMemberDTO>> callback) {
        chatsController.getPossibleChatMembers(userId, chatId).enqueue(new Callback<>() {
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
        chatsController.createGroupChat(ChatDTO).enqueue(new Callback<>() {
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

    public void removeMemberFromChat(int userId, int chatId, int creatorId, ResultCallback<Void> callback) {
        chatsController.removeMemberFromChat(chatId, userId, creatorId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onResult(Result.success(null));
                else callback.onResult(Result.error("Ошибка при удалении пользователя из чата: " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void addMembersToChat(int chatId, Set<Integer> chatMembersIds, ResultCallback<Void> callback) {
        chatsController.addMembersToChat(chatId, chatMembersIds).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error("Ошибка при добавлении пользователей в чат: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void updateChat(ChatPayloadInfo payloadInfo, ResultCallback<Void> callback) {
        chatsController.updateChat(payloadInfo.getChatId(), payloadInfo).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onResult(Result.success(null));
                else callback.onResult(Result.error("Ошибка при изменении чата:"  + payloadInfo.getChatId() + " " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchChat(int chatId, int userId, ResultCallback<ChatDTO> callback) {
        chatsController.getChat(chatId, userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ChatDTO> call, Response<ChatDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                }
                else callback.onResult(Result.error("Ошибка при получении чата:"  + chatId + " " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<ChatDTO> call, Throwable throwable) {

            }
        });
    }
}
