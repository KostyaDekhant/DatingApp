package com.example.datingappclient.retrofit.repository;

import com.example.datingappclient.model.FormDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.FormsAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormsRepository {
    private final FormsAPI formsAPI;

    public FormsRepository() {
        formsAPI = RetrofitClient.getClient().create(FormsAPI.class);
    }

    public void fetchForm(int userId, int prevUserId, ResultCallback<FormDTO> callback) {
        formsAPI.getForms(userId, prevUserId).enqueue(new Callback<>() {
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
}
