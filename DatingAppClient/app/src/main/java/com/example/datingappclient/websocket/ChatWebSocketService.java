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

    private StompClient stompClient;
    private static ChatWebSocketService webSocketService;

    private final Map<Integer, MutableLiveData<MessageDTO>> messageStreams = new HashMap<>();
    private final MutableLiveData<Integer> deletedChatIdStream = new MutableLiveData<>();
    private final MutableLiveData<Integer> updatedChatIdStream = new MutableLiveData<>();

    private final Map<Integer, Integer> historyOffsets = new HashMap<>();
    private final Map<Integer, List<MessageDTO>> historyCache = new HashMap<>();

    //private final List<MessageDTO> fullHistory = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private int offset;

    private final Map<Integer, Disposable> messageDisposables = new HashMap<>();
    private final Map<Integer, Disposable> historyDisposables = new HashMap<>();
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
    public LiveData<MessageDTO> subscribeToChat(int chatId) {
        if (messageStreams.containsKey(chatId)) {
            return messageStreams.get(chatId);
        }

        MutableLiveData<MessageDTO> liveData = new MutableLiveData<>();
        messageStreams.put(chatId, liveData);

        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP GET MESSAGE";
        Disposable disposable = stompClient.topic("/topic/messages/" + chatId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    try {
                        MessageDTO message = objectMapper.readValue(topicMessage.getPayload(), new TypeReference<MessageDTO>() {});
                        liveData.postValue(message);
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при разборе сообщения", e);
                    }
                }, throwable -> Log.e(logTag, "Ошибка подписки на чат " + chatId, throwable));
        messageDisposables.put(chatId, disposable);

        return liveData;
    }

    @SuppressLint("CheckResult")
    public void sendMessage(MessageDTO message) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP SEND MESSAGE";
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            stompClient.send("/app/send", jsonMessage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> Log.d(logTag, "Сообщение отправлено"),
                            throwable -> Log.e(logTag, "Ошибка при отправке", throwable));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // =============== HISTORY
    private final String triggerHistory = "/app/history/";

    @SuppressLint("CheckResult")
    public void subscribeHistory(int chatId, int userId, MutableLiveData<List<MessageDTO>> historyLiveData) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT HISTORY";
        List<MessageDTO> chatHistory = historyCache.getOrDefault(chatId, new ArrayList<>());
        AtomicInteger currentOffset = new AtomicInteger(historyOffsets.getOrDefault(chatId, chatHistory.size()));
        if (!historyDisposables.containsKey(chatId)) {
            Disposable disposable = stompClient.topic("/topic/" + userId + "/history/" + chatId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {
                        try {
                            List<MessageDTO> messages = objectMapper.readValue(
                                    topicMessage.getPayload(),
                                    new TypeReference<List<MessageDTO>>() {
                                    }
                            );
                            Log.i(logTag, "Получено " + messages.size() + " сообщений!");

                            chatHistory.addAll(0, messages);
                            historyLiveData.setValue(new ArrayList<>(chatHistory));
                            currentOffset.addAndGet(messages.size());
                            historyOffsets.put(chatId, currentOffset.get());

                            /*messages.addAll(chatHistory);
                            historyLiveData.setValue(messages);*/

                            if (messages.size() == Constants.MESSAGE_LIMIT) {
                                sendHistoryRequest(chatId, userId, currentOffset.get());
                            } else {
                                //historyOffsets.put(chatId, 0);
                                historyCache.put(chatId, chatHistory);
                            }
                        } catch (Exception e) {
                            Log.e(logTag, "Ошибка при разборе истории", e);
                        }
                    }, throwable -> Log.e(logTag, "Ошибка подписки на историю", throwable));

            historyDisposables.put(chatId, disposable);
        }
    }

    @SuppressLint("CheckResult")
    private void sendHistoryRequest(int chatId, int userId, int offset) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT HISTORY";
        String jsonParams = getHistoryParams(logTag, offset, userId);
        stompClient.send(triggerHistory + chatId, jsonParams)
                .subscribe(
                        () -> Log.d(logTag, "Запрос истории со смещением: " + offset),
                        throwable -> Log.e(logTag, "Ошибка запроса истории", throwable)
                );
    }

    public void triggerHistoryRequest(int chatId, int userId) {
        int offset = historyOffsets.getOrDefault(chatId, 0);
        sendHistoryRequest(chatId, userId, offset);
    }

    private String getHistoryParams(String logTag, int offset, int userId) {
        HistoryDTO params = new HistoryDTO(userId, Constants.MESSAGE_LIMIT, offset);
        String jsonParams = "";
        try {
            jsonParams = objectMapper.writeValueAsString(params);
        }
        catch (IOException e) {
            Log.e(logTag, e.getMessage());
        }
        return jsonParams;
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
                                Log.d(logTag, "Удаление чата отправлено");
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
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT DELETED";
        Disposable disposable = stompClient.topic("/topic/group_chats/" + chatId + "/deleted")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    try {
                        Log.i(logTag, "Чат удалён успешно: " + chatId);
                        deletedChatIdStream.postValue(chatId);
                        callback.onResult(Result.success(null));
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при получении удалённого чата", e);
                    }
                }, throwable -> Log.e(logTag, "Ошибка подписки на удаление чатов", throwable));
        deletedChatDisposables.put(chatId, disposable);
    }

    // =============== UPDATE CHAT
    @SuppressLint("CheckResult")
    public Disposable subscribeToChatUpdatedEvents(int chatId, ResultCallback<Boolean> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT UPDATED";
        Disposable disposable = stompClient.topic("/topic/group_chats/" + chatId + "/updated")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    try {
                        Log.i(logTag, "Чат обновлён: " + chatId);
                        callback.onResult(Result.success(true));
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при обновлении чата", e);
                        callback.onResult(Result.error("Ошибка при обновлении чата " + chatId + " " + e));
                    }
                }, throwable -> Log.e(logTag, "Ошибка подписки на обновления чатов", throwable));
        updatedChatDisposables.put(chatId, disposable);
        return disposable;
    }

    // =============== CREATE CHAT
    @SuppressLint("CheckResult")
    public Disposable subscribeToChatCreatedEvents(int userId, ResultCallback<Integer> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT CREATED";
        return stompClient.topic("/topic/group_chats/" + userId + "/created")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    try {
                        int chatId = objectMapper.readValue(message.getPayload(), Integer.class);
                        Log.i(logTag, "Ивент создания чата: " + chatId);
                        callback.onResult(Result.success(chatId));
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при создании чата", e);
                        callback.onResult(Result.error("Ошибка при создании чата " + e));
                    }
                }, throwable -> Log.e(logTag, "Ошибка глобальной подписки", throwable));
    }

    public LiveData<Integer> getDeletedChatIdStream() {
        return deletedChatIdStream;
    }

    public void unsubscribeFromChat(int chatId) {
        Disposable disposable = messageDisposables.remove(chatId);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        messageStreams.remove(chatId);
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

    public List<MessageDTO> getCacheHistory(int chatId) {
        return historyCache.getOrDefault(chatId, new ArrayList<>());
    }
}
