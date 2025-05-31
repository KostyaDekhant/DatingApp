package com.example.datingappclient.websocket;

import static com.example.datingappclient.retrofit.RetrofitClient.getHTTPClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.TokenManager;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.retrofit.RetrofitClient;
import com.example.datingappclient.retrofit.controllers.AuthController;
import com.example.datingappclient.retrofit.repository.AuthRepository;
import com.example.datingappclient.retrofit.repository.TokenRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.provider.OkHttpConnectionProvider;

public class StompClientService {
    private static final String SOCKET_URL = "wss://" + Constants.SERVER_ADDRESS + ":" + Constants.SERVER_PORT + "/datingapp";

    @Getter
    private StompClient client;
    private static StompClientService service;
    private TokenManager tokenManager;

    private StompClientService() {
        initClient();
        reconnect();
        subscribeLifecycle();
    }

    public static StompClientService getInstance() {
        if (service == null)  {
            service = new StompClientService();
        }
        return service;
    }

    private boolean forceDisconnect = false;
    public void disconnect() {
        forceDisconnect = true;
        Log.d(Constants.GLOBAL_LOG_TAG + "DISCONNECT",  "Закрываю соеденение!");
        client.disconnect();
    }

    @SuppressLint("CheckResult")
    private void subscribeLifecycle() {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP LIFECYCLE";
        // Отслеживание состояния соединения
        client.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    switch (event.getType()) {
                        case OPENED:
                            Log.d(logTag, "Соединение открыто");
                            break;
                        case ERROR:
                            Throwable exception = event.getException();
                            Log.e(logTag, "Ошибка соединения ", exception);

                            if (exception != null && isTokenExpiredError(exception)) {
                                Log.w(logTag, "Возможен истёкший токен — пробуем обновить");
                                refreshTokenAndReconnect();
                            } else {
                                Log.e(logTag, "Неизвестная ошибка — повторное подключение");
                                reconnect();
                            }
                            break;
                        case CLOSED:
                            Log.d(logTag, "Соединение закрыто");
                            if (!forceDisconnect) {
                                Log.d(logTag, "Попытка переподключения");
                                reconnect();
                            }
                            else forceDisconnect = false;
                            break;
                    }
                }, throwable -> {
                    Log.e(logTag, "Ошибка в lifecycle подписке", throwable);
                });
    }

    private void initClient() {
        Context context = DatingAppApplication.getInstance().getApplicationContext();
        // Set token
        tokenManager = new TokenManager(context);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + tokenManager.getAccessToken());
        // Get https client
        OkHttpClient customClient = getHTTPClient(context, new AuthRepository(RetrofitClient.getAuthOnlyClient(context).create(AuthController.class)), tokenManager);
        OkHttpConnectionProvider connectionProvider =
                new OkHttpConnectionProvider(SOCKET_URL, headers, customClient);
        // set client
        client = new StompClient(connectionProvider);
    }

    private void reconnect() {
        List<StompHeader> headers = List.of(new StompHeader("Authorization", "Bearer " + tokenManager.getAccessToken()));
        client.connect(headers);
    }

    private void refreshTokenAndReconnect() {
        TokenRepository tokenRepository = new TokenRepository(DatingAppApplication.getInstance().getApplicationContext());
        tokenRepository.tokenIsValid(result -> reconnect());
    }

    private boolean isTokenExpiredError(Throwable e) {
        String msg = e.getMessage();
        return msg != null && (msg.contains("401") || msg.contains("400") || msg.toLowerCase().contains("unauthorized"));
    }

    public void connect() {
        if (!client.isConnected()) {
            reconnect();
        }
    }
}
