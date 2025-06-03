package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.PictureDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.controllers.ImageController;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageRepository {
    private final ImageController imageController;

    public ImageRepository(Context context) {
        imageController = RetrofitClient.getClient(context).create(ImageController.class);
    }

    // Получение изображений пользователя
    public void fetchUserImages(int userID, ResultCallback<List<Object[]>> callback) {
        imageController.getUserImages(userID, Constants.IMAGES_LIMIT).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    callback.onResult(Result.empty());
                }
            }

            @Override
            public void onFailure(Call<List<Object[]>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка обработки данных:  " + throwable.getMessage()));
            }
        });
    }

    // Получение изображений пользователя (с лимитом)
    public void fetchUserImages(int userID, int limit, ResultCallback<List<Object[]>> callback) {
        imageController.getUserImages(userID, limit).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    callback.onResult(Result.empty());
                }
            }

            @Override
            public void onFailure(Call<List<Object[]>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка обработки данных:  " + throwable.getMessage()));
            }
        });
    }

    // Загрузка изображения
    public void uploadImage(PictureDTO picture, ResultCallback<Integer> callback) {
        imageController.uploadImage(picture.getUserId(), picture).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    callback.onResult(Result.error("Ошибка загрузки изображения: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка обработки данных:  " + throwable.getMessage()));
            }
        });
    }

    // Удаление изображения
    public void deleteImage(int imageId, ResultCallback<Void> callback) {
        imageController.deleteImage(imageId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error("Ошибка удаления изображения: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка обработки данных:  " + throwable.getMessage()));
            }
        });
    }
}