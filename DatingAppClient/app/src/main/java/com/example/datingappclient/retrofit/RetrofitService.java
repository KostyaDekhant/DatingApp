package com.example.datingappclient.retrofit;

import com.example.datingappclient.constants.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static final String BASE_URL = "http://" + Constants.SERVER_ADDRESS + ":" + Constants.SERVER_PORT;
    private static Retrofit retrofit;

    public RetrofitService() {
        initializeRetrofit();
    }
    private void initializeRetrofit() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
