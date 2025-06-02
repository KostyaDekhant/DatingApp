package com.example.datingappclient.websocket.controllers;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.ReadMessageNotification;
import com.example.datingappclient.model.ReadMessagePayload;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;
import com.example.datingappclient.websocket.StompClientService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.StompClient;

public class MessagesController {

    private static MessagesController messagesController;

    private final StompClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MessagesController() {
        client = StompClientService.getInstance().getClient();
    }

    public static MessagesController getInstance() {
        if (messagesController == null) messagesController = new MessagesController();
        return messagesController;
    }

    @SuppressLint("CheckResult")
    public void sendReadMessages(int chatId, int userId, List<Integer> readMessageIds, ResultCallback<Void> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP SEND READ MESSAGES";
        Log.e(logTag, "Begin send");
        ReadMessagePayload payload = new ReadMessagePayload(userId, chatId, readMessageIds);
        try {
            String json = objectMapper.writeValueAsString(payload);
            client.send("/app/group_chats/" + chatId + "/users/" + userId + "/messages/read", json)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                                Log.d(logTag, "Прочитанные сообщения отправлены" + json);
                                callback.onResult(Result.success(null));
                            },
                            throwable -> {
                                Log.e(logTag, "Ошибка при отправке прочитанных сообщений", throwable);
                                callback.onResult(Result.error(throwable.getMessage()));
                            });

        } catch (Exception e) {
            Log.e(logTag, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private final Map<Integer, MutableLiveData<ReadMessageNotification>> readMessageStreams = new HashMap<>();
    private final Map<Integer, Disposable> readMessageDisposables = new HashMap<>();

    /**
     * Возвращает LiveData для чтения уведомлений о прочитанных сообщениях.
     * Если подписка уже существует — повторно не подписывается.
     */
    /**
     * Возвращает LiveData для чтения уведомлений о прочитанных сообщениях.
     * Если подписка уже существует — повторно не подписывается.
     */
    public LiveData<ReadMessageNotification> subscribeToReadMessages(int chatId) {
        if (readMessageStreams.containsKey(chatId)) {
            return readMessageStreams.get(chatId);
        }

        MutableLiveData<ReadMessageNotification> liveData = new MutableLiveData<>();
        readMessageStreams.put(chatId, liveData);

        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP GET MESSAGE";

        Disposable disposable = client.topic("/topic/group_chats/" + chatId + "/read")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    try {
                        ReadMessageNotification notification = objectMapper.readValue(
                                topicMessage.getPayload(),
                                new TypeReference<ReadMessageNotification>() {}
                        );
                        Log.i(logTag, notification.toString());
                        liveData.setValue(notification);
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при разборе сообщения", e);
                    }
                }, throwable -> Log.e(logTag, "Ошибка подписки на прочитанные сообщения " + chatId, throwable));

        readMessageDisposables.put(chatId, disposable);
        return liveData;
    }
}
