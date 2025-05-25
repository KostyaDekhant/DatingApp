package com.example.datingappclient.websocket;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.ChatPayloadInfo;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class ChatWebSocketService {

    private final StompClient stompClient;
    private final Map<Integer, MutableLiveData<MessageDTO>> messageStreams = new HashMap<>();
    private final MutableLiveData<Integer> deletedChatIdStream = new MutableLiveData<>();
    private final MutableLiveData<ChatDTO> updatedChatStream = new MutableLiveData<>();
    private final List<MessageDTO> fullHistory = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private int offset;

    @SuppressLint("CheckResult")
    public ChatWebSocketService(String token) {
        // 1. Инициализация клиента
        stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "ws://" + Constants.SERVER_ADDRESS + ":" + Constants.SERVER_PORT + "/datingapp"
        );

        // 2. Подключение с токеном
        reconnect(token);

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
                            Log.e(logTag, "Ошибка соединения ", event.getException());
                            reconnect(token);
                            break;
                        case CLOSED:
                            Log.d(logTag, "Соединение закрыто " + event.getMessage());
                            break;
                    }
                }, throwable -> {
                    Log.e(logTag, "Ошибка в lifecycle подписке", throwable);
                });
    }

    private void reconnect(String token) {
        List<StompHeader> headers = List.of(new StompHeader("Authorization", "Bearer " + token));
        stompClient.connect(headers);
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
                }, throwable -> Log.e(logTag, "Ошибка подписки на чат " + chatId, throwable));

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

    private final String triggerHistory = "/app/history/";
    @SuppressLint("CheckResult")
    public void getHistory(int chatId, int userId, MutableLiveData<List<MessageDTO>> historyLiveData) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT HISTORY";

        String jsonParams = getHistoryParams(logTag, offset, userId);

        stompClient.topic("/topic/" + userId + "/history/" + chatId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    try {
                        List<MessageDTO> messages = objectMapper.readValue(
                                topicMessage.getPayload(),
                                new TypeReference<List<MessageDTO>>() {}
                        );
                        Log.i(logTag, "Получено " + messages.size() + " сообщений!");
                        fullHistory.addAll(messages);
                        messages.addAll(fullHistory);
                        historyLiveData.postValue(messages);
                        if (messages.size() == Constants.MESSAGE_LIMIT) {
                            offset += Constants.MESSAGE_LIMIT;
                            stompClient.send(triggerHistory + chatId, getHistoryParams(logTag, offset, userId))
                                    .subscribe(
                                            () -> Log.d(logTag, "История запрошена"),
                                            throwable -> Log.e(logTag, "Ошибка при запросе истории", throwable)
                                    );
                        }
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при разборе истории", e);
                    }
                }, throwable -> Log.e(logTag, "Ошибка подписки на историю", throwable));

        stompClient.send(triggerHistory + chatId, jsonParams)
                .subscribe(
                        () -> Log.d(logTag, "Первый запрос отправлен"),
                        throwable -> Log.e(logTag, "Ошибка отправки первого запроса", throwable)
                );
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

    @SuppressLint("CheckResult")
    public void sendDeleteGroupChat(int chatId, int userId, ResultCallback<Void> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "DELETE CHAT";
        ChatPayloadInfo payload = new ChatPayloadInfo(chatId, userId, null, null);
        try {
            String json = objectMapper.writeValueAsString(payload);
            stompClient.send("/app/group_chats/remove", json)
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
        stompClient.topic("/topic/group_chats/" + chatId + "/deleted")
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
    }

    @SuppressLint("CheckResult")
    public void sendUpdateGroupChat(ChatPayloadInfo updateInfo, ResultCallback<Void> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "UPDATE CHAT";
        try {
            String json = objectMapper.writeValueAsString(updateInfo);
            stompClient.send("/app/group_chats/update", json)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                                Log.d(logTag, "Обновление чата отправлено, image size = " + updateInfo.getImage().length);
                                callback.onResult(Result.success(null));
                            },
                            error -> {
                                Log.e(logTag, "Ошибка обновления чата", error);
                                callback.onResult(Result.error("Ошибка обновления чата " + error));
                            });
        } catch (Exception e) {
            Log.e(logTag, "Ошибка сериализации", e);
        }
    }

    @SuppressLint("CheckResult")
    public Disposable subscribeToChatUpdatedEvents(int chatId, ResultCallback<Boolean> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT UPDATED";
        return stompClient.topic("/topic/group_chats/" + chatId + "/updated")
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
    }

    @SuppressLint("CheckResult")
    public void sendCreateGroupChat(ChatDTO chat, ResultCallback<Void> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "CREATE CHAT";
        try {
            String json = objectMapper.writeValueAsString(parseChat(chat));
            Log.d(logTag, json);
            stompClient.send("/app/group_chats/create", json)
                    .subscribe(() -> {
                                Log.d(logTag, "Создание чата отправлено");
                                callback.onResult(Result.success(null));
                            },
                            error -> {
                                Log.e(logTag, "Ошибка создания чата", error);
                                callback.onResult(Result.error("Ошибка создания чата" + error));
                            });
        } catch (Exception e) {
            Log.e(logTag, "Ошибка сериализации", e);
        }
    }

    private ChatDTO parseChat(ChatDTO chat) {
        List<ChatMemberDTO> chatMembers = new ArrayList<>();
        for (ChatMemberDTO chatMember : chat.getChatInfo().getMembers()) {
            chatMembers.add(new ChatMemberDTO(null, chatMember.getId(), null, false, false));
        }
        ChatInfoDTO chatInfo = new ChatInfoDTO(chat.getChatInfo().getCreatedBy(), chat.getChatInfo().getCreatedAt(), chatMembers, chat.getChatInfo().getIsGroup());
        return new ChatDTO(chat.getId(), chat.getName(), chat.getLastMessage(), chat.getImage(), chatInfo);
    }

    @SuppressLint("CheckResult")
    public Disposable subscribeToChatCreatedEvents(int userId, ResultCallback<ChatDTO> callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "STOMP CHAT CREATED";
        return stompClient.topic("/topic/group_chats/" + userId + "/created")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    try {
                        ChatDTO chat = objectMapper.readValue(message.getPayload(), ChatDTO.class);
                        updatedChatStream.postValue(chat);
                        Log.i(logTag, "Создание чата: " + chat.getId());
                        callback.onResult(Result.success(chat));
                    } catch (Exception e) {
                        Log.e(logTag, "Ошибка при создании чата", e);
                        callback.onResult(Result.error("Ошибка при создании чата " + e));
                    }
                }, throwable -> Log.e(logTag, "Ошибка глобальной подписки", throwable));
    }

    public LiveData<Integer> getDeletedChatIdStream() {
        return deletedChatIdStream;
    }

    public LiveData<ChatDTO> getUpdatedChatStream() {
        return updatedChatStream;
    }

    public void checkConnection() {

    }
}
