package com.example.datingappclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTokens(String accessToken, String refreshToken) {
        sharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public void saveAccessToken(String accessToken) {
        sharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .apply();
    }

    public void saveRefreshToken(String refreshToken) {
        Log.d("TOKEN_MANAGER", "Сохраняем refresh: " + refreshToken);
        sharedPreferences.edit()
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public void saveUserId(int userId) {
        sharedPreferences.edit()
                .putInt(KEY_USER_ID, userId)
                .apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, 0);
    }

    public boolean isValid() {
        return getAccessToken() != null && getRefreshToken() != null && getUserId() != 0;
    }

    public void clearTokens() {
        Log.d("TOKEN_MANAGER", "Удаляем токены" );
        sharedPreferences.edit()
                .clear()
                .apply();
    }
}
