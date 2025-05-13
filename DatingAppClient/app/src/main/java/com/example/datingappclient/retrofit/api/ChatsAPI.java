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
}
