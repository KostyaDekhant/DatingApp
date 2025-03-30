package com.example.datingappclient.retrofit.repository;

import android.util.Log;

import com.example.datingappclient.model.PictureDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.ImageAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageRepository {
    private final ImageAPI imageAPI;

    public ImageRepository() {
        imageAPI = RetrofitClient.getClient().create(ImageAPI.class);
    }

    public interface ImagesCallback {
        void onSuccess(List<Object[]> images);
        void onError(String errorMessage);
    }

    public interface UploadCallback {
        void onSuccess(int imageId);
        void onError(String errorMessage);
    }

    public interface DeleteCallback {
        void onSuccess(int responseCode);
        void onError(String errorMessage);
    }

    // Получение изображений пользователя
    public void fetchUserImages(int userID, ImagesCallback callback) {
        imageAPI.getUserImages(userID).enqueue(new Callback<List<Object[]>>() {
            @Override
            public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Ошибка получения изображений: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Object[]>> call, Throwable throwable) {
                Log.e("ImageRepository", "Ошибка сети при получении изображений", throwable);
                callback.onError("Ошибка сети: " + throwable.getMessage());
            }
        });
    }

    // Загрузка изображения
    public void uploadImage(PictureDTO picture, UploadCallback callback) {
        imageAPI.uploadImage(picture).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Ошибка загрузки изображения: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                Log.e("ImageRepository", "Ошибка сети при загрузке изображения", throwable);
                callback.onError("Ошибка сети: " + throwable.getMessage());
            }
        });
    }

    // Удаление изображения
    public void deleteImage(int imageID, DeleteCallback callback) {
        imageAPI.deleteImage(imageID).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Ошибка удаления изображения: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                Log.e("ImageRepository", "Ошибка сети при удалении изображения", throwable);
                callback.onError("Ошибка сети: " + throwable.getMessage());
            }
        });
    }
}