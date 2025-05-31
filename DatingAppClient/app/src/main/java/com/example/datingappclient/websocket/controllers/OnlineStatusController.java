package com.example.datingappclient.websocket.controllers;

import android.util.Log;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.OnlineStatusDTO;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;
import com.example.datingappclient.websocket.StompClientService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.PATCH;
import ua.naiksoftware.stomp.StompClient;

public class OnlineStatusController {

    private final StompClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OnlineStatusController() {
        client = StompClientService.getInstance().getClient();
    }

    public Disposable subscribeToUpdateOnlineStatus(ResultCallback<OnlineStatusDTO> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP ONLINE STATUS";
        return client.topic("/topic/online")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    try {
                        OnlineStatusDTO onlineStatus = objectMapper.readValue(message.getPayload(), new TypeReference<OnlineStatusDTO>(){});
                        Log.i(logTag, "Статус обновлён: " + onlineStatus.toString());
                        callback.onResult(Result.success(onlineStatus));
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при обновлении статуса", e);
                        callback.onResult(Result.error("Ошибка при обновлении статуса " + e));
                    }
                }, throwable -> Log.e(logTag, "Ошибка подписки на обновления статуса", throwable));
    }
}
