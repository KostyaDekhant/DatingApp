package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.GroupChatDTO;
import com.example.datingappclient.model.dto.GroupChatInfoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatsAPI {
    @GET("api/chats/{userId}")
    Call<List<ChatDTO>> getChats(@Path("userId") int userId);

    @GET("api/chats/{chatId}/users/{userId}")
    Call<ChatDTO> getChat(@Path("userId") int userId, @Path("chatId") int chatId);

    // Создание чата с юзером
    @POST("api/chats")
    Call<Integer> createChat(@Query("userA") int userId, @Query("userB") int likerId);

    /* === Group Chats === */
    @GET("api/group_chats/{chatId}")
    Call<GroupChatInfoDTO> getChatInfo(@Path("chatId") int chatId);

    @GET("api/group_chats/users/{userId}")
    Call< List<GroupChatDTO>> getGroupChats(@Path("userId") int userId);

    @POST("/api/group_chats")
    Call <Void> addChat(@Body GroupChatDTO groupChatDto);

    @POST("/api/group_chats/{chatId}/users/{userId}")
    Call <Void> addMemberToChat(@Path("chatId") int chatId, @Path("userId") int userId);
}
