package com.example.datingappclient.retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServerAPI {

    @GET("/api/cat")
    Call<JsonObject> getData();
}
