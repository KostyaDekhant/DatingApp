package com.example.datingappclient.retrofit.repository;

import android.content.Context;

import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.dto.CompanyInfoDTO;
import com.example.datingappclient.model.dto.AuthDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.controllers.UserController;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final UserController userController;

    public UserRepository(Context context) {
        userController = RetrofitClient.getClient(context).create(UserController.class);
    }

    /* === Methods === */
    // Получение информации о пользователе
    public void fetchUserInfo(int userId, ResultCallback<UserDTO> callback){
        userController.getUser(userId).enqueue(new Callback<>() {
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
        userController.login(loginInfo).enqueue(new Callback<>() {
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
        userController.signup(authData).enqueue(new Callback<>() {
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
        userController.updateUser(userData.getId(), userData).enqueue(new Callback<>() {
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
        userController.getUserCompany(userId).enqueue(new Callback<>() {
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
        userController.updateUserCompany(userId, companyInfo).enqueue(new Callback<>() {
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

    public void fetchOnlineUsers(ResultCallback<List<Integer>> callback) {
        userController.getOnlineUsers().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) callback.onResult(Result.success(response.body()));
                    else callback.onResult(Result.empty());
                }
                else callback.onResult(Result.error("Ошибка запроса онлайна пользователей!"));
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable throwable) {
                callback.onResult(Result.error("Ошибка сети или ошибка при обработке данных: " + throwable.getMessage()));
            }});
    }
}