package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.ChatPayloadInfo;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.ChatInfoDTO;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatsAPI {
    /* === Group Chats === */
    @GET("api/group_chats/{chatId}/info")
    Call<ChatInfoDTO> getChatInfo(@Path("chatId") int chatId);

    @GET("api/group_chats/{chatId}")
    Call<ChatDTO> getChat(@Path("chatId") int chatId, @Query("userId") int userId);

    @GET("api/users/{userId}/group_chats")
    Call<List<ChatDTO>> getChats(@Path("userId") int userId, @Query("limit") int limit, @Query("offset") int offset);

    //https://localhost:8081/api/group_chats/21/avatar?userId=110
    @GET("api/group_chats/{chatId}/avatar")
    Call<ChatDTO> getChatAvatar(@Path("chatId") int chatId, @Query("userId") int userId);

    @GET("api/group_chats/{chatId}/users/{userId}")
    Call<List<ChatMemberDTO>> getPossibleChatMembers(@Path("userId") int userId, @Path("chatId") int chatId);

    @POST("api/group_chats/create")
    Call<Integer> createGroupChat(@Body ChatDTO chat);

    @POST("api/group_chats/{chatId}/members")
    Call<Void> addMembersToChat(@Path("chatId") int chatId, @Query("userIds") Set<Integer> chatMembersIds);

    @DELETE("api/group_chats/{chatId}/members/{userId}")
    Call<Void> removeMemberFromChat(@Path("chatId") int chatId, @Path("userId") int userId, @Query("creatorId") int creatorId);

    @PATCH("api/group_chats/{chatId}")
    Call<Void> updateChat(@Path("chatId") int chatId, @Body ChatPayloadInfo payloadInfo);
}
