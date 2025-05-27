package com.example.datingappclient.retrofit.api;

import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.TokenRefreshRequest;
import com.example.datingappclient.model.dto.AuthDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthAPI {
    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthDTO authDTO);

    @POST("auth/signup")
    Call<AuthResponse> signup(@Body AuthDTO authDTO);

    @POST("/auth/refresh")
    Call<AuthResponse> refreshToken(@Body TokenRefreshRequest tokenRefreshRequest);

    @POST("/logout")
    Call<Void> logout(@Query("userId") Integer userId);
}
