package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.GroupChatDTO;
import com.example.datingappclient.model.dto.GroupChatInfoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GroupChatAPI {
    /* === Group Chats === */
    @GET("api/group_chats/{chatId}")
    Call<GroupChatInfoDTO> getChatInfo(@Path("chatId") int chatId);

    @GET("api/group_chats/users/{userId}")
    Call<List<GroupChatDTO>> getGroupChats(@Path("userId") int userId);

    @GET("api/group_chats/{chatId}/users/{userId}")
    Call<List<ChatMemberDTO>> getChatMembers(@Path("userId") int userId, @Path("chatId") int chatId);

    @POST("/api/group_chats")
    Call <Integer> createGroupChat(@Body GroupChatDTO groupChatDto);

    @POST("/api/group_chats/{chatId}/users/{userId}")
    Call <Void> addMemberToChat(@Path("chatId") int chatId, @Path("userId") int userId);
}
