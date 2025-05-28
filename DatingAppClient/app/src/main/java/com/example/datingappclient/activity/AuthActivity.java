package com.example.datingappclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.splashscreen.SplashScreen;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.datingappclient.R;
import com.example.datingappclient.TokenManager;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.fragments.SigninFragment;
import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.TokenRefreshRequest;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.api.AuthAPI;
import com.example.datingappclient.retrofit.repository.AuthRepository;
import com.example.datingappclient.retrofit.repository.TokenRepository;

import java.io.IOException;

import retrofit2.Retrofit;

public class AuthActivity extends AppCompatActivity {

    /* === Repository === */
    private TokenRepository tokenRepository;
    private TokenManager tokenManager;

    /* === Other === */
    private boolean isAppReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        // Верни true — сплеш остаётся, false — исчезает
        splashScreen.setKeepOnScreenCondition(() -> !isAppReady);

        super.onCreate(savedInstanceState);

        tokenManager = new TokenManager(getApplicationContext());

        AuthResponse authResponse = getAuthResponse();
        if (authResponse.isExists()) {
            tokenRepository = new TokenRepository(getApplicationContext());
            checkToken(authResponse);
        }
        else startAuth();
    }

    private void checkToken(AuthResponse authResponse) {
        String logTag = Constants.GLOBAL_LOG_TAG + "AUTH CHECK";
        tokenRepository.tokenIsValid(result -> {
            switch (result.status) {
                case SUCCESS:
                    if (result.data) {
                        Log.i(logTag, "Токен валиден, вход...");
                        startMainActivity(authResponse);
                    }
                    else {
                        // токен невалиден, пробуем обновить
                        Log.i(logTag, "Токен невалиден, пробуем обновить...");
                        tryRefreshToken();
                    }
                    break;
                case ERROR:
                    Log.e(logTag, "Ошибка при проверке токена: " + result.error);
                    startAuth();
                    break;
            }
        });
    }

    private void startAuth() {
        isAppReady = true;
        String logTag = Constants.GLOBAL_LOG_TAG + "AUTH CHECK";
        // пользователь не вошёл — показать экран входа
        Log.i(logTag, "Пользователь не авторизован, переход на страницу авторизации!");
        setContentView(R.layout.activity_auth);
        openFragment(new SigninFragment());
    }

    private void tryRefreshToken() {
        String logTag = Constants.GLOBAL_LOG_TAG + "TOKEN REFRESH";
        TokenManager tokenManager = new TokenManager(getApplicationContext());

        Retrofit authRetrofit = RetrofitClient.getAuthOnlyClient(this);
        AuthRepository authRepository = new AuthRepository(authRetrofit.create(AuthAPI.class));

        new Thread(() -> {
            String refreshToken = tokenManager.getRefreshToken();
            if (refreshToken == null) {
                Log.w(logTag, "Нет refresh token — переход к авторизации");
                runOnUiThread(this::startAuth);
                return;
            }

            try {
                AuthResponse newTokens = authRepository.refreshTokenSync(new TokenRefreshRequest(refreshToken));
                tokenManager.saveAccessToken(newTokens.getToken());
                tokenManager.saveUserId(newTokens.getUserId());
                tokenManager.saveRefreshToken(newTokens.getRefreshToken());

                Log.i(logTag, "Токен успешно обновлён — перепроверяем");

                runOnUiThread(() -> checkToken(newTokens));

            } catch (IOException e) {
                Log.e(logTag, "Не удалось обновить токен: " + e.getMessage());
                runOnUiThread(this::startAuth);
            }
        }).start();
    }

    private AuthResponse getAuthResponse() {
        String token = tokenManager.getAccessToken();
        int userId = tokenManager.getUserId();
        return new AuthResponse(token, null, userId);
    }

    public void startMainActivity(AuthResponse authResponse) {
        // Сохраням токен и id в хранилище
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
        finish();
    }

    public void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.auth_fragment_container, fragment).commit();
    }
}
