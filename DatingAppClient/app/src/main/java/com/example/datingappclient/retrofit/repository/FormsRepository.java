package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.dto.FormDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.FormsAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class FormsRepository {
    private final FormsAPI formsAPI;

    public FormsRepository(Context context) {
        formsAPI = RetrofitClient.getClient(context).create(FormsAPI.class);
    }

    public void fetchForm__old(int userId, int prevUserId, ResultCallback<FormDTO> callback) {
        formsAPI.getForms__old(userId, prevUserId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<FormDTO> call, Response<FormDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) callback.onResult(Result.empty());
                    else callback.onResult(Result.success(response.body()));
                }
                else callback.onResult(Result.error("Ошибка получения анкеты: " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<FormDTO> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchForms(int userId,
                           int ageMin,
                           int ageMax,
                           int heightMin,
                           int heightMax,
                           String gender,
                           int limit,
                           int offset,
                           ResultCallback<List<FormDTO>> callback

    ) {
        formsAPI.getForms(userId, ageMin, ageMax, heightMin, heightMax, gender, limit, offset).enqueue(new Callback<List<FormDTO>>() {
            @Override
            public void onResponse(Call<List<FormDTO>> call, Response<List<FormDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                }
                else callback.onResult(Result.error("Ошибка получения анкет: " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<List<FormDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }
}
