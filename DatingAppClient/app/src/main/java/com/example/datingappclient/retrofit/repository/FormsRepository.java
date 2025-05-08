package com.example.datingappclient.retrofit.repository;

import com.example.datingappclient.model.FormDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.FormsAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormsRepository {
    private final FormsAPI formsAPI;

    public FormsRepository() {
        formsAPI = RetrofitClient.getClient().create(FormsAPI.class);
    }

    public interface FormCallback {
        void onSuccess(FormDTO form);
        void onEmpty(String message);
        void onError(String errorMessage);
    }

    public void fetchForm(int userId, int prevUserId, FormCallback callback) {
        formsAPI.getForms(userId, prevUserId).enqueue(new Callback<FormDTO>() {
            @Override
            public void onResponse(Call<FormDTO> call, Response<FormDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) callback.onEmpty("Анкет нет!");
                    else callback.onSuccess(response.body());
                }
                else callback.onError("Ошибка получения анкеты: " + response.code() + " " + response.message());
            }

            @Override
            public void onFailure(Call<FormDTO> call, Throwable throwable) {
                callback.onError("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage());
            }
        });
    }
}
