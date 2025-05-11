package com.example.datingappclient.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datingappclient.R;
import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.retrofit.repository.TokenRepository;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DURATION = 1500;

    private TokenRepository tokenRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.core.splashscreen.SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        tokenRepository = new TokenRepository(getApplicationContext());

        // Можно показать layout с логотипом или анимацией
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            String token = prefs.getString("token", null);

            if (token != null && !token.isEmpty()) {
                tokenRepository.tokenIsValid(result -> {
                    if (result.data) navigateToMain();
                    else navigateToAuth();
                });

            } else {
                navigateToAuth();
            }
        }, SPLASH_DURATION);
    }

    private AuthResponse getAuthResponse() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        String token = prefs.getString("token", null);
        return new AuthResponse(token, userId);
    }

    private void navigateToMain() {
        AuthResponse authResponse = getAuthResponse();
        startActivity(new Intent(this, MainActivity.class).putExtra("authResponse", authResponse));
        finish();
    }

    private void navigateToAuth() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }
}
