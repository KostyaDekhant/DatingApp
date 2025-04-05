package com.example.datingappclient.retrofit;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.utils.LocalDateDeserializer;
import com.example.datingappclient.utils.LocalDateSerializer;
import com.example.datingappclient.utils.TimestampDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://" + Constants.SERVER_ADDRESS + ":" + Constants.SERVER_PORT;
    private static Retrofit retrofit;
    public static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampDeserializer())
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
