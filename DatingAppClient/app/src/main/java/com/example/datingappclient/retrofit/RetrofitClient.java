package com.example.datingappclient.retrofit;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.utils.gson.LocalDateDeserializer;
import com.example.datingappclient.utils.gson.LocalDateSerializer;
import com.example.datingappclient.utils.gson.TimestampDeserializer;
import com.example.datingappclient.utils.gson.TimestampSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;
import java.time.LocalDate;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://" + Constants.SERVER_ADDRESS + ":" + Constants.SERVER_PORT;

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampDeserializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampSerializer())
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getHTTPClient(context))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getHTTPClient(Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
            String token = prefs.getString("token", null);

            Request.Builder requestBuilder = original.newBuilder();

            if (token != null) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        return httpClient.build();
    }


}
