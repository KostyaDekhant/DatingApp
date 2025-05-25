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
    @GET("api/group_chats/info")
    Call<ChatInfoDTO> getChatInfo(@Query("chatId") int chatId);

    @GET("api/group_chats/{chatId}")
    Call<ChatDTO> getChat(@Path("chatId") int chatId, @Query("userId") int userId);

    @GET("api/group_chats")
    Call<List<ChatDTO>> getChats(@Query("userId") int userId, @Query("limit") int limit, @Query("offset") int offset);

    @GET("api/group_chats/avatars")
    Call<List<ChatDTO>> getChatsAvatars(@Query("userId") int userId, @Query("chatIds") List<Integer> chatIds);

    @GET("api/group_chats/{chatId}/users/{userId}")
    Call<List<ChatMemberDTO>> getChatMembers(@Path("userId") int userId, @Path("chatId") int chatId);

    @POST("/api/group_chats")
    Call<Integer> createGroupChat(@Body ChatDTO chat);

    @POST("/api/group_chats/{chatId}/users/{userId}")
    Call<Void> addMemberToChat(@Path("chatId") int chatId, @Path("userId") int userId);

    @POST("/api/group_chats/users")
    Call<Void> addMembersToChat(@Query("chatId") int chatId, @Query("userIds") Set<Integer> chatMembersIds);

    @DELETE("/api/group_chats/{chatId}/creator/{creatorId}")
    Call<Void> deleteChat(@Path("chatId") int chatId, @Path("creatorId") int creatorId);

    @DELETE("/api/group_chats/{chatId}/users/{userId}/creator/{creatorId}")
    Call<Void> removeMemberFromChat(@Path("chatId") int chatId, @Path("userId") int userId, @Path("creatorId") int creatorId);

    @PATCH("/api/group_chats")
    Call<Void> updateChat(@Body ChatPayloadInfo payloadInfo);
}
