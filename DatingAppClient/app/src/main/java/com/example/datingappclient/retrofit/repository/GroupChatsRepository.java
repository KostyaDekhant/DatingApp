package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.GroupChatDTO;
import com.example.datingappclient.model.dto.GroupChatInfoDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.GroupChatAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupChatsRepository {
    private final GroupChatAPI groupChatAPI;

    public GroupChatsRepository(Context context) {
        groupChatAPI = RetrofitClient.getClient(context).create(GroupChatAPI.class);
    }


    /* === Group Chats === */
    public void fetchChatInfo(int chatId, ResultCallback<GroupChatInfoDTO> callback) {
        groupChatAPI.getChatInfo(chatId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GroupChatInfoDTO> call, Response<GroupChatInfoDTO> response) {
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
            public void onFailure(Call<GroupChatInfoDTO> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchUserGroupChats(int userId, ResultCallback<List<GroupChatDTO>> callback) {
        groupChatAPI.getGroupChats(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<GroupChatDTO>> call, Response<List<GroupChatDTO>> response) {
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
            public void onFailure(Call<List<GroupChatDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchChatMembers(int userId, int chatId, ResultCallback<List<ChatMemberDTO>> callback) {
        groupChatAPI.getChatMembers(userId, chatId).enqueue(new Callback<List<ChatMemberDTO>>() {
            @Override
            public void onResponse(Call<List<ChatMemberDTO>> call, Response<List<ChatMemberDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                }
                else {
                    callback.onResult(Result.error("Не удалось получить участников чата! " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<ChatMemberDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void createChat(GroupChatDTO groupChatDto, ResultCallback<Integer> callback) {
        groupChatAPI.createGroupChat(groupChatDto).enqueue(new Callback<>() {
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
        groupChatAPI.addMemberToChat(chatId, userId).enqueue(new Callback<>() {
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
}
