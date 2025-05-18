package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.PictureDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.ImageAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageRepository {
    private final ImageAPI imageAPI;

    public ImageRepository(Context context) {
        imageAPI = RetrofitClient.getClient(context).create(ImageAPI.class);
    }

    // Получение изображений пользователя
    public void fetchUserImages(int userID, ResultCallback<List<Object[]>> callback) {
        imageAPI.getUserImages(userID, Constants.IMAGES_LIMIT).enqueue(new Callback<>() {
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
        imageAPI.uploadImage(picture).enqueue(new Callback<>() {
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
        imageAPI.deleteImage(imageId).enqueue(new Callback<>() {
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