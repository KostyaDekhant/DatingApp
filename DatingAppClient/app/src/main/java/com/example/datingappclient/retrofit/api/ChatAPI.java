package com.example.datingappclient.retrofit.api;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatAPI {
    @GET("api/chat_users/{pk_user}")
    Call<List<Object[]>> getChats(@Path("pk_user") int id);

    @POST("api/chats")
    Call<Integer> createChat(@Query("pk_user") int userID, @Query("pk_user1") int likerID);
}
