package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.UserDTO;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/*

    Методы API для работы с сущностями пользователя
    -- описание --

*/
public interface UserAPI {
    @GET("api/users/{id}")
    Call<UserDTO> getUser(@Path("id") int id);

    @PATCH("api/users")
    Call<Void> updateUser(@Body UserDTO userDTO);

    @POST("api/login")
    Call<Integer> login(@Body JsonObject jsonObject);

    @POST("api/signup")
    Call<Integer> signup(@Body JsonObject jsonObject);
}
