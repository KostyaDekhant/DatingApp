package com.example.datingappclient.websocket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.TokenManager;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.ChatPayloadInfo;
import com.example.datingappclient.model.Event;
import com.example.datingappclient.model.dto.HistoryDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.StompClient;

public class ChatWebSocketService {

    private final StompClient stompClient;
    private static ChatWebSocketService webSocketService;

    private final MutableLiveData<Integer> deletedChatIdStream = new MutableLiveData<>();
    private final MutableLiveData<Integer> updatedChatIdStream = new MutableLiveData<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<Integer, Disposable> updatedChatDisposables = new HashMap<>();
    private final Map<Integer, Disposable> deletedChatDisposables = new HashMap<>();

    private ChatWebSocketService() {
        stompClient = StompClientService.getInstance().getClient();
    }

    public static ChatWebSocketService getInstance() {
        if (webSocketService == null)
            webSocketService = new ChatWebSocketService();
        return webSocketService;
    }

    // =============== MESSAGES
    @SuppressLint("CheckResult")
    public LiveData<Event<MessageDTO>> subscribeToChat(int chatId) {
        return StompClientService
                .getInstance()
                .getSubscriptionManager()
                .subscribeToChatMessages(chatId);
    }

    @SuppressLint("CheckResult")
    public void sendMessage(MessageDTO message) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP SEND MESSAGE";
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            stompClient.send("/app/send", jsonMessage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> Log.d(logTag, "Сообщение успешно отправлено через сокеты"),
                            throwable -> Log.e(logTag, "Ошибка при отправке", throwable));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // =============== HISTORY
    @SuppressLint("CheckResult")
    public LiveData<List<MessageDTO>> subscribeHistory(int chatId, int userId) {
        return StompClientService
                .getInstance()
                .getSubscriptionManager()
                .subscribeToChatHistory(chatId, userId);
    }

    // =============== DELETE CHAT
    @SuppressLint("CheckResult")
    public void sendDeleteGroupChat(int chatId, int userId, ResultCallback<Void> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "DELETE CHAT";
        ChatPayloadInfo payload = new ChatPayloadInfo(chatId, userId, null, null);
        try {
            String json = objectMapper.writeValueAsString(payload);
            stompClient.send("/app/group_chats/" + chatId + "/remove", json)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                                Log.d(logTag, "Удаление чата отправлено\n" + json);
                                callback.onResult(Result.success(null));
                            },
                            error -> {
                                Log.e(logTag, "Ошибка при удалении чата", error);
                                callback.onResult(Result.error("Ошибка при удалении чата " + error));
                            });
        } catch (Exception e) {
            Log.e(logTag, "Ошибка сериализации", e);
        }
    }

    @SuppressLint("CheckResult")
    public void subscribeToChatDeletedEvents(int chatId, ResultCallback<Void> callback) {
        StompClientService
                .getInstance()
                .getSubscriptionManager()
                .subscribeToChatDeletedEvent(chatId, deletedId -> {
                    deletedChatIdStream.postValue(deletedId);
                    callback.onResult(Result.success(null));
                });
    }

    // =============== UPDATE CHAT
    @SuppressLint("CheckResult")
    public Disposable subscribeToChatUpdatedEvents(int chatId, ResultCallback<Boolean> callback) {
        Disposable disposable = StompClientService
                .getInstance()
                .getSubscriptionManager()
                .subscribeToChatUpdatedEvent(chatId, updatedId -> callback.onResult(Result.success(true)));
        updatedChatDisposables.put(chatId, disposable);
        return disposable;
    }

    // =============== CREATE CHAT
    @SuppressLint("CheckResult")
    public Disposable subscribeToChatCreatedEvents(int userId, ResultCallback<Integer> callback) {
        return  StompClientService
                .getInstance()
                .getSubscriptionManager()
                .subscribeToChatCreatedEvent(userId, chatId -> callback.onResult(Result.success(chatId)));
    }

    public LiveData<Integer> getDeletedChatIdStream() {
        return deletedChatIdStream;
    }

    public void unsubscribeFromChat(int chatId) {
        /*Disposable disposable = messageDisposables.remove(chatId);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        messageStreams.remove(chatId);*/
    }

    public void unsubscribeFromChatUpdatedEvents(int chatId) {
        Disposable disposable = updatedChatDisposables.remove(chatId);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void unsubscribeFromChatDeletedEvents(int chatId) {
        Disposable disposable = deletedChatDisposables.remove(chatId);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


}
