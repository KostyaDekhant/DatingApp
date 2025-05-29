package com.example.datingappclient.retrofit.controllers;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TokenController {
    @GET("api/token/healthcheck")
    public Call<Void> tokenIsValid();
}
