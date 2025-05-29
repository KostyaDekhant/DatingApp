package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.dto.FormDTO;
import com.example.datingappclient.model.dto.FormsParametersDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.controllers.FormsController;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormsRepository {
    private final FormsController formsController;

    public FormsRepository(Context context) {
        formsController = RetrofitClient.getClient(context).create(FormsController.class);
    }

    public void fetchForms(FormsParametersDTO params, ResultCallback<List<FormDTO>> callback) {
        formsController.getForms(params).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<FormDTO>> call, Response<List<FormDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() != 0) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                } else
                    callback.onResult(Result.error("Ошибка получения анкет: " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<List<FormDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }
}
