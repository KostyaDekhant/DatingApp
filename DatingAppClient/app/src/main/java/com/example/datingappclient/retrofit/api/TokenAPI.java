package com.example.datingappclient.retrofit.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TokenAPI {
    @GET("api/token/healthcheck")
    public Call<Void> tokenIsValid();
}
