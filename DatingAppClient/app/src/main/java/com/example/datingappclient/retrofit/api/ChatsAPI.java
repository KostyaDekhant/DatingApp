package com.example.datingappclient.retrofit.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatsAPI {
    @GET("api/chats/{pk_user}")
    Call<List<Object[]>> getChats(@Path("pk_user") int userId);

    // Создание чата с юзером
    @POST("api/chats")
    Call<Integer> createChat(@Query("userA") int userId, @Query("userB") int likerId);
}
