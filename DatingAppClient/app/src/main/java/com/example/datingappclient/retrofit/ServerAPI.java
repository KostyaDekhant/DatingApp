package com.example.datingappclient.retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerAPI {

    @GET("api/user")
    Call<JsonObject> getUser(@Query("id") int id);

    @POST("api/user")
    Call<Boolean> updateUser(@Body JsonObject jsonObject);

    @POST("api/login")
    Call<Integer> login(@Body JsonObject jsonObject);
}
