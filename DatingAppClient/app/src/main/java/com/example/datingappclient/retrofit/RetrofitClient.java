package com.example.datingappclient.retrofit;

import android.content.Context;

import com.example.datingappclient.TokenManager;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.retrofit.api.AuthAPI;
import com.example.datingappclient.retrofit.repository.AuthRepository;
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
    private static Retrofit authRetrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampDeserializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampSerializer())
                    .setLenient()
                    .create();

            Retrofit authRetrofit = RetrofitClient.getAuthOnlyClient(context);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getHTTPClient(context, new AuthRepository(authRetrofit.create(AuthAPI.class)), new TokenManager(context)))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getAuthOnlyClient(Context context) {
        if (authRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampDeserializer())
                    .registerTypeAdapter(Timestamp.class, new TimestampSerializer())
                    .setLenient()
                    .create();

            OkHttpClient client = new OkHttpClient.Builder().build(); // без интерсепторов и аутентификаторов

            authRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return authRetrofit;
    }

    private static OkHttpClient getHTTPClient(Context context, AuthRepository authRepository, TokenManager tokenManager) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            String token = tokenManager.getAccessToken();

            Request.Builder requestBuilder = original.newBuilder();

            if (token != null) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        })
                .authenticator(new TokenAuthenticator(authRepository, tokenManager));

        return httpClient.build();
    }


}
