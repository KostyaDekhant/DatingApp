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
import retrofit2.http.Query;

public interface ChatsAPI {
    /* === Group Chats === */
    @GET("api/group_chats/info")
    Call<ChatInfoDTO> getChatInfo(@Query("chatId") int chatId);

    @GET("api/group_chats")
    Call<List<ChatDTO>> getGroupChats(@Query("userId") int userId, @Query("limit") int limit, @Query("offset") int offset);

    @GET("api/group_chats/{chatId}/users/{userId}")
    Call<List<ChatMemberDTO>> getChatMembers(@Path("userId") int userId, @Path("chatId") int chatId);

    @POST("/api/group_chats")
    Call <Integer> createGroupChat(@Body ChatDTO chat);

    @POST("/api/group_chats/{chatId}/users/{userId}")
    Call <Void> addMemberToChat(@Path("chatId") int chatId, @Path("userId") int userId);
}
