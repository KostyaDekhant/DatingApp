package com.example.datingappclient.websocket;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datingappclient.constants.Constants;
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
import java.util.function.Consumer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.StompClient;

public class SubscriptionManager {
    private final StompClient client;
    private final Map<String, Disposable> subscriptions = new HashMap<>();
    private final Map<String, Consumer<String>> handlers = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String logTag = Constants.GLOBAL_LOG_TAG + "SUBSCRIPTIONS";

    public SubscriptionManager(@NonNull StompClient client) {
        this.client = client;
    }

    public void subscribe(String topic, @NonNull Consumer<String> callback) {
        if (subscriptions.containsKey(topic)) {
            Log.d(logTag, "Уже подписан на: " + topic);
            return;
        }

        Log.d(logTag, "Подписка на: " + topic);
        Disposable disposable = client.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        stompMessage -> callback.accept(stompMessage.getPayload()),
                        throwable -> Log.e(logTag, "Ошибка подписки на " + topic, throwable)
                );

        subscriptions.put(topic, disposable);
        handlers.put(topic, callback);
    }

    public void unsubscribe(String topic) {
        if (subscriptions.containsKey(topic)) {
            subscriptions.get(topic).dispose();
            subscriptions.remove(topic);
            handlers.remove(topic);
            Log.d(logTag, "Отписка от: " + topic);
        }
    }

    public void clearAll() {
        for (Disposable d : subscriptions.values()) {
            d.dispose();
        }
        subscriptions.clear();
        handlers.clear();
        Log.d(logTag, "Все подписки удалены");
    }

    public void resubscribeAll() {
        Log.d(logTag, "Восстанавливаю подписки");
        Map<String, Consumer<String>> handlersCopy = new HashMap<>(handlers);
        clearAll();
        for (Map.Entry<String, Consumer<String>> entry : handlersCopy.entrySet()) {
            subscribe(entry.getKey(), entry.getValue());
        }
    }

    // ===== Специальная подписка для сообщений чата =====

    private final Map<String, MutableLiveData<MessageDTO>> messageStreams = new HashMap<>();
    public LiveData<MessageDTO> subscribeToChatMessages(int chatId) {
        String topic = "/topic/messages/" + chatId;

        // Если уже подписан — возвращаем LiveData
        if (messageStreams.containsKey(topic)) {
            return messageStreams.get(topic);
        }

        MutableLiveData<MessageDTO> liveData = new MutableLiveData<>();
        messageStreams.put(topic, liveData);

        subscribe(topic, payload -> {
            try {
                MessageDTO message = objectMapper.readValue(payload, MessageDTO.class);
                Log.d(logTag, "Получено сообщение: " + message);

                // === Обновляем кэш ===
                List<MessageDTO> chatHistory = historyCache.getOrDefault(chatId, new ArrayList<>());
                chatHistory.add(message);
                historyCache.put(chatId, chatHistory);

                // === Обновляем offset ===
                int currentOffset = historyOffsets.getOrDefault(chatId, 0);
                historyOffsets.put(chatId, currentOffset + 1);

                // === Передаём в LiveData ===
                liveData.postValue(message);

            } catch (Exception e) {
                Log.e(logTag, "Ошибка парсинга сообщения для чата " + chatId, e);
            }
        });

        return liveData;
    }

    // ===== Специальная подписка для истории сообщений чата =====

    private final Map<Integer, MutableLiveData<List<MessageDTO>>> historyStreams = new HashMap<>();
    private final Map<Integer, List<MessageDTO>> historyCache = new HashMap<>();
    private final Map<Integer, Integer> historyOffsets = new HashMap<>();
    private final Map<Integer, Integer> userIdsForHistory = new HashMap<>();

    public LiveData<List<MessageDTO>> subscribeToChatHistory(int chatId, int userId) {
        String topic = "/topic/" + userId + "/history/" + chatId;

        if (historyStreams.containsKey(chatId)) {
            return historyStreams.get(chatId);
        }

        MutableLiveData<List<MessageDTO>> liveData = new MutableLiveData<>();
        historyStreams.put(chatId, liveData);
        userIdsForHistory.put(chatId, userId);

        subscribe(topic, payload -> {
            try {
                List<MessageDTO> messages = objectMapper.readValue(
                        payload,
                        new TypeReference<List<MessageDTO>>() {}
                );
                Log.i(logTag, "Получено " + messages.size() + " сообщений из истории");

                List<MessageDTO> chatHistory = historyCache.getOrDefault(chatId, new ArrayList<>());
                int currentOffset = historyOffsets.getOrDefault(chatId, chatHistory.size());

                chatHistory.addAll(0, messages);
                historyCache.put(chatId, chatHistory);
                liveData.postValue(new ArrayList<>(chatHistory));
                historyOffsets.put(chatId, currentOffset + messages.size());

                if (messages.size() == Constants.MESSAGE_LIMIT) {
                    sendHistoryRequest(chatId, userId, currentOffset + messages.size());
                }

            } catch (Exception e) {
                Log.e(logTag, "Ошибка при разборе истории", e);
            }
        });

        // Сразу отправим триггер после подписки
        int currentOffset = historyOffsets.getOrDefault(chatId, 0);
        //sendHistoryRequest(chatId, userId, currentOffset);
        new Handler(Looper.getMainLooper()).postDelayed(() -> sendHistoryRequest(chatId, userId, currentOffset), 150);

        return liveData;
    }

    @SuppressLint("CheckResult")
    private void sendHistoryRequest(int chatId, int userId, int offset) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT HISTORY";
        String jsonParams = getHistoryParams(logTag, offset, userId);
        client.send("/app/history/" + chatId, jsonParams)
                .subscribe(
                        () -> Log.d(logTag, "Запрос истории со смещением: " + offset),
                        throwable -> Log.e(logTag, "Ошибка запроса истории", throwable)
                );
    }

    private String getHistoryParams(String logTag, int offset, int userId) {
        HistoryDTO params = new HistoryDTO(userId, Constants.MESSAGE_LIMIT, offset);
        try {
            return objectMapper.writeValueAsString(params);
        } catch (IOException e) {
            Log.e(logTag, "Ошибка сериализации параметров", e);
            return "{}";
        }
    }

    // ===== Специальная подписка для ивентов удаления чата =====

    public void subscribeToChatDeletedEvent(int chatId, @NonNull Consumer<Integer> onDeleted) {
        String topic = "/topic/group_chats/" + chatId + "/deleted";
        String logTag = Constants.GLOBAL_LOG_TAG + "CHAT DELETED";

        subscribe(topic, payload -> {
            try {
                Log.i(logTag, "Чат удалён: " + chatId);
                onDeleted.accept(chatId);
            } catch (Exception e) {
                Log.e(logTag, "Ошибка обработки события удаления чата", e);
            }
        });
    }

    // ===== Специальная подписка для ивентов обновления чата =====

    public Disposable subscribeToChatUpdatedEvent(int chatId, @NonNull Consumer<Integer> onUpdated) {
        String topic = "/topic/group_chats/" + chatId + "/updated";
        String logTag = Constants.GLOBAL_LOG_TAG + "CHAT UPDATED";

        Disposable disposable = client.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        message -> {
                            try {
                                Log.i(logTag, "Ивент обновления чата: " + chatId);
                                onUpdated.accept(chatId);
                            } catch (Exception e) {
                                Log.e(logTag, "Ошибка обработки события обновления", e);
                            }
                        },
                        throwable -> Log.e(logTag, "Ошибка подписки на обновление чата", throwable)
                );

        subscriptions.put(topic, disposable);
        return disposable;
    }

    // ===== Специальная подписка для ивентов создания чата =====

    public Disposable subscribeToChatCreatedEvent(int userId, @NonNull Consumer<Integer> onChatCreated) {
        String topic = "/topic/group_chats/" + userId + "/created";
        String logTag = Constants.GLOBAL_LOG_TAG + "CHAT CREATED";

        Disposable disposable = client.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        message -> {
                            try {
                                int chatId = new ObjectMapper().readValue(message.getPayload(), Integer.class);
                                Log.i(logTag, "Создан новый чат: " + chatId);
                                onChatCreated.accept(chatId);
                            } catch (Exception e) {
                                Log.e(logTag, "Ошибка обработки события создания чата", e);
                            }
                        },
                        throwable -> Log.e(logTag, "Ошибка подписки на создание чатов", throwable)
                );

        subscriptions.put(topic, disposable);
        return disposable;
    }

}
