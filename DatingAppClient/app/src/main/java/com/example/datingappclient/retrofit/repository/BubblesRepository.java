package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.dto.CategoryDTO;
import com.example.datingappclient.model.dto.InterestDTO;
import com.example.datingappclient.model.dto.UserInterestDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.controllers.BubblesController;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BubblesRepository {
    private final BubblesController bubblesController;

    public BubblesRepository(Context context) {
        bubblesController = RetrofitClient.getClient(context).create(BubblesController.class);
    }

    /* === Methods === */
    public void fetchCategories(ResultCallback<List<CategoryDTO>> callback) {
        bubblesController.getListCategories().enqueue(new Callback<>() {
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
        bubblesController.getListInterests().enqueue(new Callback<>() {
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
        bubblesController.getListInterestsByCategory(categoryId).enqueue(new Callback<>() {
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
        bubblesController.getListUserInterestsByCategory(userId, categoryId).enqueue(new Callback<>() {
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
        bubblesController.addUserInterests(userId, interest).enqueue(new Callback<>() {
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
        bubblesController.deleteUserInterests(userId, interest).enqueue(new Callback<>() {
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

    public void fetchUserInterests(int userId, ResultCallback<List<UserInterestDTO>> callback) {
        bubblesController.getListUserInterests(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<UserInterestDTO>> call, Response<List<UserInterestDTO>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                }
                else callback.onResult(Result.error("Ошибка при запросе интересов пользователя!" + userId));
            }

            @Override
            public void onFailure(Call<List<UserInterestDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }
}
