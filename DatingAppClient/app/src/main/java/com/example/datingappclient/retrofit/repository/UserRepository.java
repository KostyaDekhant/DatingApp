package com.example.datingappclient.retrofit.repository;

import android.util.Log;

import com.example.datingappclient.model.UserDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.UserAPI;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final UserAPI userAPI;

    public UserRepository() {
        userAPI = RetrofitClient.getClient().create(UserAPI.class);
    }

    /* === Methods === */
    // Получение информации о пользователе
    public void fetchUserInfo(int userId, ResultCallback<UserDTO> callback){
        userAPI.getUser(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                }
                else {
                    callback.onResult(Result.error("Ошибка получения пользователя: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных:" + throwable.getMessage()));
            }
        });
    }

    // Логин пользователя
    public void login(JsonObject loginData, ResultCallback<Integer> callback) {
        userAPI.login(loginData).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    callback.onResult(Result.error("Ошибка входа: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                Log.e("UserRepository", "Ошибка при входе", throwable);
                callback.onResult(Result.error("Ошибка: " + throwable.getMessage()));
            }
        });
    }

    // Регистрация пользователя
    public void signup(JsonObject signupData, ResultCallback<Integer> callback) {
        userAPI.signup(signupData).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    callback.onResult(Result.error("Ошибка регистрации: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка: " + throwable.getMessage()));
            }
        });
    }

    public void updateUser (UserDTO userData, ResultCallback<Void> callback) {
        userAPI.updateUser(userData).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error("Ошибка обновления пользователя: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("User Repository", "Ошибка при обновлении пользователя", throwable);
                callback.onResult(Result.error("Ошибка: " + throwable.getMessage()));
            }
        });
    }
}