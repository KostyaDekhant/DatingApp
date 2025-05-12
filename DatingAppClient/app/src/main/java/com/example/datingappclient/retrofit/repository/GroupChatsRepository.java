package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.dto.GroupChatDTO;
import com.example.datingappclient.model.dto.GroupChatInfoDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.ChatsAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class GroupChatsRepository {
    private final ChatsAPI chatsAPI;

    public GroupChatsRepository(Context context) {
        chatsAPI = RetrofitClient.getClient(context).create(ChatsAPI.class);
    }


    /* === Group Chats === */
    /*@GET("api/group_chats/{chatId}")
    Call<GroupChatInfoDTO> getChatInfo(@Path("chatId") int chatId);

    @GET("api/group_chats/users/{userId}")
    Call<List<GroupChatDTO>> getGroupChats(@Path("userId") int userId);

    @POST("/api/group_chats")
    Call <Void> addChat(@Body GroupChatDTO groupChatDto);

    @POST("/api/group_chats/{chatId}/users/{userId}")
    Call <Void> addMemberToChat(@Path("chatId") int chatId, @Path("userId") int userId);*/

    private void fetchChatInfo(int chatId, ResultCallback<GroupChatInfoDTO> callback) {
        chatsAPI.getChatInfo(chatId).enqueue(new Callback<>() {
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

    private void fetchUserGroupChats(int userId, ResultCallback<List<GroupChatDTO>> callback) {
        chatsAPI.getGroupChats(userId).enqueue(new Callback<>() {
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

    private void addChat(GroupChatDTO groupChatDto, ResultCallback<Void> callback) {
        chatsAPI.addChat(groupChatDto).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error("Ошибка при создании группового чата: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    private void addMemberToChat(int chatId, int userId, ResultCallback<Void> callback) {
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
}
