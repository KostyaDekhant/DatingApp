package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.dto.CategoryDTO;
import com.example.datingappclient.model.dto.InterestDTO;
import com.example.datingappclient.model.dto.UserInterestDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.BubblesAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BubblesRepository {
    private final BubblesAPI bubblesAPI;

    public BubblesRepository(Context context) {
        bubblesAPI = RetrofitClient.getClient(context).create(BubblesAPI.class);
    }

    /* === Methods === */
    public void fetchCategories(ResultCallback<List<CategoryDTO>> callback) {
        bubblesAPI.getListCategories().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<CategoryDTO>> call, Response<List<CategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                }
                else {
                    callback.onResult(Result.error("Ошибка получения категорий: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<CategoryDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchInterests(ResultCallback<List<InterestDTO>> callback) {
        bubblesAPI.getListInterests().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<InterestDTO>> call, Response<List<InterestDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                }
                else {
                    callback.onResult(Result.error("Ошибка получения интересов: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<InterestDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchInterestsByCategory(int categoryId, ResultCallback<List<InterestDTO>> callback) {
        bubblesAPI.getListInterestsByCategory(categoryId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<InterestDTO>> call, Response<List<InterestDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                }
                else {
                    callback.onResult(Result.error("Ошибка получения интересов по категориям: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<InterestDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void fetchUserInterestsByCategory(int userId, int categoryId, ResultCallback<List<UserInterestDTO>> callback) {
        bubblesAPI.getListUserInterestsByCategory(userId, categoryId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<UserInterestDTO>> call, Response<List<UserInterestDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    callback.onResult(Result.error("Ошибка получения интересов по категориям: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<UserInterestDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void addUserInterest(int userId, List<UserInterestDTO> interest, ResultCallback<Void> callback) {
        bubblesAPI.addUserInterests(userId, interest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onResult(Result.success(null));
                else callback.onResult(Result.error("Ошибка при добавлении интересов!"));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void deleteUserInterests(int userId, List<UserInterestDTO> interest, ResultCallback<Void> callback) {
        bubblesAPI.deleteUserInterests(userId, interest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onResult(Result.success(null));
                else callback.onResult(Result.error("Ошибка при удалении интересов!"));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }
}
