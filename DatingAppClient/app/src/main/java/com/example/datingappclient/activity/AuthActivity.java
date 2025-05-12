package com.example.datingappclient.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.core.splashscreen.SplashScreen;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.fragments.SigninFragment;
import com.example.datingappclient.model.AuthResponse;

public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        String logTag = Constants.GLOBAL_LOG_TAG + "AUTH CHECK";
        AuthResponse authResponse = getAuthResponse();
        if (authResponse.isExists()) {
            // пользователь уже вошёл — открыть основной экран
            Log.i(logTag, "Пользователь авторизован: userId=" + authResponse.getUserId() + " token=" + authResponse.getToken());
            startMainActivity(authResponse);
        } else {
            // пользователь не вошёл — показать экран входа
            Log.i(logTag, "Пользователь не авторизован, переход на страницу авторизации!");
            setContentView(R.layout.activity_auth);
            getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, new SigninFragment()).commit();
        }
    }

    private AuthResponse getAuthResponse() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        String token = prefs.getString("token", null);
        return new AuthResponse(token, userId);
    }

    public void startMainActivity(AuthResponse authResponse) {
        // Сохраням токен и id в хранилище
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit()
                .putString("token", authResponse.getToken()) // или .putString("token", ...)
                .putInt("userId", authResponse.getUserId()) // или .putString("token", ...)
                .apply();
        startActivity(new Intent(AuthActivity.this, MainActivity.class).putExtra("authResponse", authResponse));
        finish();
    }

    public void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, fragment).commit();
    }
}
