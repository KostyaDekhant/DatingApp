package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.ChatInfoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatsAPI {
    /* === Group Chats === */
    @GET("api/group_chats/{chatId}")
    Call<ChatInfoDTO> getChatInfo(@Path("chatId") int chatId);

    @GET("api/group_chats/users/{userId}")
    Call<List<ChatDTO>> getGroupChats(@Path("userId") int userId);

    @GET("api/group_chats/{chatId}/users/{userId}")
    Call<List<ChatMemberDTO>> getChatMembers(@Path("userId") int userId, @Path("chatId") int chatId);

    @POST("/api/group_chats")
    Call <Integer> createGroupChat(@Body ChatDTO chat);

    @POST("/api/group_chats/{chatId}/users/{userId}")
    Call <Void> addMemberToChat(@Path("chatId") int chatId, @Path("userId") int userId);
}
