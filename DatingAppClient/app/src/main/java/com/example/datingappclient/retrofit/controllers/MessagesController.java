package com.example.datingappclient.retrofit.controllers;

import com.example.datingappclient.model.dto.MessageDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessagesController {
    @GET("api/group_chats/{chatId}/users/{userId}/messages/unread")
    Call<List<MessageDTO>> getPersonalUnreadMessages(@Path("chatId") int chatId, @Path("userId") int userId);

    @GET("api/group_chats/{chatId}/messages/unread")
    Call<List<MessageDTO>> getChatUnreadMessages(@Path("chatId") int chatId, @Query("userId") int userId);
}
