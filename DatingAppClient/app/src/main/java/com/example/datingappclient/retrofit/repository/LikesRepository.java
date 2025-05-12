package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.dto.LikeDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.LikesAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesRepository {
    private final LikesAPI likesAPI;

    public LikesRepository(Context context) {
        likesAPI = RetrofitClient.getClient(context).create(LikesAPI.class);
    }

    /* === Methods === */
    public void fetchUserLikes(int userId, ResultCallback<List<LikeDTO>> callback) {
        likesAPI.getLikes(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<LikeDTO>> call, Response<List<LikeDTO>> response) {
                if (!response.isSuccessful()) callback.onResult(Result.error("Не удалось получить лайки: " + response.code() + " " + response.message()));
                if (response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                }
                else {
                    callback.onResult(Result.empty());
                }
            }

            @Override
            public void onFailure(Call<List<LikeDTO>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + call + " " + throwable.getMessage()));
            }
        });
    }

    public void sendLike(LikeDTO likeDTO, ResultCallback<Integer> callback) {
        likesAPI.sendLike(likeDTO).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.code() == 409) callback.onResult(Result.error("Лайк уже был поставлен. " + response.message()));
                else if (!response.isSuccessful()) callback.onResult(Result.error("Не удалось поставить лайк! " + response.code() + " " + response.message()));
                if (response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                }
                else {
                    callback.onResult(Result.error( "Не удалось поставить лайк! " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void deleteLike(LikeDTO likeDTO, ResultCallback<Integer> callback) {
        likesAPI.deleteLike(likeDTO).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                }
                else {
                    callback.onResult(Result.error("Ошибка удаления лайка: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }
}
