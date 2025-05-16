package com.example.datingappclient.websocket;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.MessageDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class ChatWebSocketService {

    private final StompClient stompClient;
    private final Map<Integer, MutableLiveData<MessageDTO>> messageStreams = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressLint("CheckResult")
    public ChatWebSocketService(String token) {
        // 1. Инициализация клиента
        stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "ws://" + Constants.SERVER_ADDRESS + ":" + Constants.SERVER_PORT + "/datingapp"
        );

        // 2. Подключение с токеном
        List<StompHeader> headers = List.of(new StompHeader("Authorization", "Bearer " + token));
        stompClient.connect(headers);

        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP HEALTHCHECK";
        // 3. Отслеживание состояния соединения
        stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    switch (event.getType()) {
                        case OPENED:
                            Log.d(logTag, "Соединение открыто");
                            break;
                        case ERROR:
                            Log.e(logTag, "Ошибка соединения", event.getException());
                            break;
                        case CLOSED:
                            Log.d(logTag, "Соединение закрыто");
                            break;
                    }
                }, throwable -> {
                    Log.e(logTag, "Ошибка в lifecycle подписке", throwable);
                });
    }

    /**
     * Подписка на сообщения чата по chatId.
     */
    @SuppressLint("CheckResult")
    public LiveData<MessageDTO> subscribeToChat(int chatId) {
        if (messageStreams.containsKey(chatId)) {
            return messageStreams.get(chatId);
        }

        MutableLiveData<MessageDTO> liveData = new MutableLiveData<>();
        messageStreams.put(chatId, liveData);

        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP GET MESSAGE";
        stompClient.topic("/topic/messages/" + chatId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    try {
                        MessageDTO message = objectMapper.readValue(topicMessage.getPayload(), new TypeReference<MessageDTO>() {});
                        liveData.postValue(message);
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при разборе сообщения", e);
                    }
                }, throwable -> {
                    Log.e(logTag, "Ошибка подписки на чат " + chatId, throwable);
                });

        return liveData;
    }

    @SuppressLint("CheckResult")
    public void sendMessage(MessageDTO message) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP SEND MESSAGE";
        try {
            String jsonMessage = objectMapper.writeValueAsString(message); // ✅ это корректный JSON
            stompClient.send("/app/send", jsonMessage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> Log.d(logTag, "Сообщение отправлено"),
                            throwable -> Log.e(logTag, "Ошибка при отправке", throwable));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("CheckResult")
    public void getHistory(int chatId, MutableLiveData<List<MessageDTO>> historyLiveData) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT HISTORY";
        stompClient.topic("/topic/history/" + chatId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    try {
                        List<MessageDTO> messages = objectMapper.readValue(
                                topicMessage.getPayload(),
                                new TypeReference<List<MessageDTO>>() {}
                        );
                        historyLiveData.postValue(messages);
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при разборе истории", e);
                    }
                }, throwable -> {
                    Log.e(logTag, "Ошибка подписки на историю", throwable);
                });

        // Триггерим сервер, чтобы он отправил историю
        stompClient.send("/app/history/" + chatId).subscribe();
    }

    /**
     * Закрытие соединения (вызывать при завершении работы Activity/Fragment).
     */
    public void disconnect() {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP DISCONNECT";
        if (stompClient != null && stompClient.isConnected()) {
            stompClient.disconnect();
            Log.d(logTag, "Отключение от WebSocket");
        }
    }
}
