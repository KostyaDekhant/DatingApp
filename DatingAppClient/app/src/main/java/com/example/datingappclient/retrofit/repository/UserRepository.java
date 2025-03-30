package com.example.datingappclient.retrofit.repository;

import android.util.Log;

import com.example.datingappclient.model.UserDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.UserAPI;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final UserAPI userAPI;

    public UserRepository() {
        userAPI = RetrofitClient.getClient().create(UserAPI.class);
    }

    public interface UserCallback {
        void onSuccess(UserDTO user);
        void onError(String errorMessage);
    }

    public interface LoginCallback {
        void onSuccess(int userId);
        void onError(String errorMessage);
    }

    public interface SignupCallback {
        void onSuccess(int userId);
        void onError(String errorMessage);
    }

    // Получение информации о пользователе
    public void fetchUserInfo(int userID, UserCallback callback) {
        userAPI.getUser(userID).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO userinfo = response.body();
                    callback.onSuccess(userinfo);
                } else {
                    callback.onError("Ошибка получения пользователя: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable throwable) {
                Log.e("UserRepository", "Ошибка сети или ошибка при обработке данных", throwable);
                callback.onError("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage());
            }
        });
    }

    // Логин пользователя
    public void login(JsonObject loginData, LoginCallback callback) {
        userAPI.login(loginData).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Ошибка входа: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                Log.e("UserRepository", "Ошибка сети при входе", throwable);
                callback.onError("Ошибка сети: " + throwable.getMessage());
            }
        });
    }

    // Регистрация пользователя
    public void signup(JsonObject signupData, SignupCallback callback) {
        userAPI.signup(signupData).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Ошибка регистрации: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable throwable) {
                Log.e("UserRepository", "Ошибка сети при регистрации", throwable);
                callback.onError("Ошибка сети: " + throwable.getMessage());
            }
        });
    }
}