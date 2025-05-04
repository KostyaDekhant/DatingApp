package com.example.datingappclient.retrofit.repository;

import android.util.Log;

import com.example.datingappclient.model.CategoryDTO;
import com.example.datingappclient.model.InterestDTO;
import com.example.datingappclient.model.UserInterestDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.BubblesAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BubblesRepository {
    private final BubblesAPI bubblesAPI;

    public BubblesRepository() {
        bubblesAPI = RetrofitClient.getClient().create(BubblesAPI.class);
    }

    public interface CategoryCallback {
        void onSuccess(List<CategoryDTO> categories);
        void onError(String errorMessage);
    }

    public interface InterestCallback {
        void onSuccess(List<InterestDTO> interests);
        void onError(String errorMessage);
    }

    public interface UserInterestCallback {
        void onSuccess(List<UserInterestDTO> interests);
        void onError(String errorMessage);
    }

    public void fetchCategories(CategoryCallback callback) {
        bubblesAPI.getListCategories().enqueue(new Callback<List<CategoryDTO>>() {
            @Override
            public void onResponse(Call<List<CategoryDTO>> call, Response<List<CategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryDTO> categories = response.body();
                    callback.onSuccess(categories);
                }
                else {
                    callback.onError("Ошибка получения категорий: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<CategoryDTO>> call, Throwable throwable) {
                Log.e("BubbleRepository. Categories", "Ошибка сети или ошибка при обработке данных" + call.toString(), throwable);
                callback.onError("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage());
            }
        });
    }

    public void fetchInterests(InterestCallback callback) {
        bubblesAPI.getListInterests().enqueue(new Callback<List<InterestDTO>>() {
            @Override
            public void onResponse(Call<List<InterestDTO>> call, Response<List<InterestDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<InterestDTO> interests = response.body();
                    callback.onSuccess(interests);
                }
                else {
                    callback.onError("Ошибка получения интересов: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<InterestDTO>> call, Throwable throwable) {
                Log.e("BubbleRepository. Interests", "Ошибка сети или ошибка при обработке данных" + call.toString(), throwable);
                callback.onError("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage());
            }
        });
    }

    public void fetchInterestsByCategory(int categoryId, InterestCallback callback) {
        bubblesAPI.getListInterestsByCategory(categoryId).enqueue(new Callback<List<InterestDTO>>() {
            @Override
            public void onResponse(Call<List<InterestDTO>> call, Response<List<InterestDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<InterestDTO> interests = response.body();
                    callback.onSuccess(interests);
                }
                else {
                    callback.onError("Ошибка получения интересов по категориям: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<InterestDTO>> call, Throwable throwable) {
                Log.e("BubbleRepository. Interests", "Ошибка сети или ошибка при обработке данных" + call.toString(), throwable);
                callback.onError("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage());
            }
        });
    }

    public void fetchUserInterestsByCategory(int userId, int categoryId, UserInterestCallback callback) {
        bubblesAPI.getListUserInterestsByCategory(userId, categoryId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<UserInterestDTO>> call, Response<List<UserInterestDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserInterestDTO> interests = response.body();
                    callback.onSuccess(interests);
                } else {
                    callback.onError("Ошибка получения интересов по категориям: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<UserInterestDTO>> call, Throwable throwable) {
                Log.e("BubbleRepository. Interests", "Ошибка сети или ошибка при обработке данных" + call.toString(), throwable);
                callback.onError("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage());
            }
        });
    }
}
