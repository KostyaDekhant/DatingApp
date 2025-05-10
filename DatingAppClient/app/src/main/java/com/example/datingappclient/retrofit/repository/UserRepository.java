package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.CompanyInfoDTO;
import com.example.datingappclient.model.AuthDTO;
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

    public UserRepository(Context context) {
        userAPI = RetrofitClient.getClient(context).create(UserAPI.class);
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
                    callback.onResult(Result.error("Ошибка получения пользователя: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных:" + throwable.getMessage() + "\n" + call));
            }
        });
    }

    // Логин пользователя
    public void login(AuthDTO loginInfo, ResultCallback<AuthResponse> callback) {
        userAPI.login(loginInfo).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    callback.onResult(Result.error("Ошибка входа: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    // Регистрация пользователя
    public void signup(AuthDTO authData, ResultCallback<AuthResponse> callback) {
        userAPI.signup(authData).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(response.body()));
                } else {
                    callback.onResult(Result.error("Ошибка регистрации: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    // Обновление пользователя
    public void updateUser (UserDTO userData, ResultCallback<Void> callback) {
        userAPI.updateUser(userData).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error("Ошибка обновления пользователя: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    // Получение данных о компании, в которой работает пользователь
    public void fetchUserCompanyInfo(int userId, ResultCallback<CompanyInfoDTO> callback) {
        userAPI.getUserCompany(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<CompanyInfoDTO> call, Response<CompanyInfoDTO> response) {
                if (response.isSuccessful())  {
                    if (response.body() != null) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                }
            }

            @Override
            public void onFailure(Call<CompanyInfoDTO> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }

    public void updateUserCompanyInfo(int userId, CompanyInfoDTO companyInfo, ResultCallback<Void> callback) {
        userAPI.updateUserCompany(userId, companyInfo).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onResult(Result.success(null));
                else callback.onResult(Result.error("Ошибка при обновлении информации о компании: " + response.code() + " " + response.message()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }
        });
    }
}