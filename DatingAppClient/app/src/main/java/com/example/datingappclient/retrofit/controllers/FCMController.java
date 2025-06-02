package com.example.datingappclient.retrofit.controllers;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FCMController {
    @POST("/api/fcm/register")
    Call<Void> registerToken(@Query("userId") int userId, @Query("token") String token);
}
