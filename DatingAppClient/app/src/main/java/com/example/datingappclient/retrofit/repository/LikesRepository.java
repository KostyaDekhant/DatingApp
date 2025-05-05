package com.example.datingappclient.retrofit.repository;

import com.example.datingappclient.model.LikeDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.LikesAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesRepository {
    private final LikesAPI likesAPI;

    public LikesRepository() {
        likesAPI = RetrofitClient.getClient().create(LikesAPI.class);
    }

    /* === Interfaces === */
    public interface LikesCallback {
        void onSuccess(List<LikeDTO> likes);
        void onEmpty(String message);
        void onError(String errorMessage);
    }

    public interface SendLikeCallback {
        void onSuccess(Integer likeId);
        void onError(String errorMessage);
    }

    public interface DislikeCallback {
        void onSuccess(Integer deleteLikesCount);
        void onError(String errorMessage);
    }

    /* === Methods === */
    public void fetchUserLikes(int userId, LikesCallback callback) {
        likesAPI.getLikes(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<LikeDTO>> call, Response<List<LikeDTO>> response) {
                if (!response.isSuccessful()) callback.onError("Не удалось получить лайки: " + response.message());
                if (response.body() != null) {
                    callback.onSuccess(response.body());
                }
                else {
                    callback.onEmpty("Лайки не найдены: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<LikeDTO>> call, Throwable throwable) {
                callback.onError("Ошибка сети или ошибка при обработке данных: " + call + " " + throwable.getMessage());
            }
        });
    }

    public void sendLike(LikeDTO likeDTO, SendLikeCallback callback) {
        likesAPI.sendLike(likeDTO).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.code() == 409) callback.onError("Лайк уже был поставлен. " + response.message());
                else if (!response.isSuccessful()) callback.onError("Не удалось поставить лайк! " + response.code() + " " + response.message());
                if (response.body() != null) {
                    callback.onSuccess(response.body());
                }
                else {
                    callback.onError("Не удалось поставить лайк! " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                callback.onError("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage());
            }
        });
    }

    public void deleteLike(LikeDTO likeDTO, DislikeCallback callback) {
        likesAPI.deleteLike(likeDTO).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                }
                else {
                    callback.onError("Ошибка удаления лайка: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                callback.onError("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage());
            }
        });
    }
}
