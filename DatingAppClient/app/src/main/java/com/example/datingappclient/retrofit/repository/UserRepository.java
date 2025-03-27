package com.example.datingappclient.retrofit.repository;

import android.util.Log;

import com.example.datingappclient.model.User;
import com.example.datingappclient.model.User1;
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
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public interface UserModelCallback {
        void onSuccess(User1 user);
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
        userAPI.getUser(userID).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject userinfo = response.body();
                    User user = new User(
                            userID,
                            userinfo.get("name").getAsString(),
                            userinfo.get("description").getAsString(),
                            userinfo.get("age").getAsString()
                    );
                    callback.onSuccess(user);
                } else {
                    callback.onError("Ошибка получения пользователя: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Log.e("UserRepository", "Ошибка сети", throwable);
                callback.onError("Ошибка сети: " + throwable.getMessage());
            }
        });
    }

    public void fetchUserModelInfo(int userID, UserModelCallback callback) {
        userAPI.getUserModel(userID).enqueue(new Callback<User1>() {
            @Override
            public void onResponse(Call<User1> call, Response<User1> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User1 userinfo = response.body();
                   /* User user = new User(
                            userID,
                            userinfo.get("name").getAsString(),
                            userinfo.get("description").getAsString(),
                            userinfo.get("age").getAsString()
                    );*/
                    callback.onSuccess(userinfo);
                } else {
                    callback.onError("Ошибка получения пользователя: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User1> call, Throwable throwable) {
                Log.e("UserRepository", "Ошибка сети", throwable);
                callback.onError("Ошибка сети: " + throwable.getMessage());
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