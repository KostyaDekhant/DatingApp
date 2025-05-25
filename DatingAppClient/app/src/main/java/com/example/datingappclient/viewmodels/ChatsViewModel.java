package com.example.datingappclient.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datingappclient.model.ChatPayloadInfo;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;
import com.example.datingappclient.websocket.ChatWebSocketService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class ChatsViewModel extends ViewModel {
    private final MutableLiveData<List<ChatDTO>> chats = new MutableLiveData<>();
    private final ChatWebSocketService webSocketService;

    public ChatsViewModel(String token) {
        this.webSocketService = new ChatWebSocketService(token);
        initSubscriptions();
    }

    private void initSubscriptions() {
        // Реакция на удаление чата
        webSocketService.getDeletedChatIdStream().observeForever(chatId -> {
            if (chatId != null) {
                deleteChat(chatId);
            }
        });
    }

    public void subscribeToDeleteChat(int chatId) {
        webSocketService.subscribeToChatDeletedEvents(chatId, result -> {});
    }

    public void subscribeToDeleteChat(int chatId, ResultCallback<Void> callback) {
        webSocketService.subscribeToChatDeletedEvents(chatId, callback);
    }

    public void subscribeToUpdateChat(int chatId) {
        webSocketService.subscribeToChatUpdatedEvents(chatId, result -> {});
    }

    public Disposable subscribeToUpdateChat(int chatId, ResultCallback<Boolean> callback) {
        return webSocketService.subscribeToChatUpdatedEvents(chatId, callback);
    }

    public void subscribeToCreateChat(int userId) {
        webSocketService.subscribeToChatCreatedEvents(userId,result -> {});
    }

    public Disposable subscribeToCreateChat(int userId, ResultCallback<ChatDTO> callback) {
        return webSocketService.subscribeToChatCreatedEvents(userId,callback);
    }


    public void updateOrAddChat(ChatDTO chat) {
        List<ChatDTO> currentList = chats.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        boolean updated = false;
        for (int i = 0; i < currentList.size(); i++) {
            ChatDTO existingChat = currentList.get(i);
            if (existingChat.getId().equals(chat.getId())) {
                currentList.set(i, chat); // заменили
                updated = true;
                break;
            }
        }

        if (!updated) {
            currentList.add(0, chat); // новый чат в начало
        }

        chats.setValue(currentList);
    }

    /**
     * Получить список всех чатов
     */
    public LiveData<List<ChatDTO>> getChats() {
        return chats;
    }

    /**
     * Установить (обновить) список чатов
     */
    public void setChats(List<ChatDTO> chatList) {
        chats.setValue(chatList);
    }

    public void addChats(List<ChatDTO> newChats) {
        List<ChatDTO> currentList = chats.getValue();

        if (currentList == null || currentList.isEmpty()) {
            chats.setValue(new ArrayList<>(newChats));
        } else {
            currentList.addAll(newChats);
            chats.setValue(currentList);
        }
    }

    public void addChats(ChatDTO chat) {
        List<ChatDTO> currentList = chats.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        currentList.add(chat);
        chats.setValue(currentList);
    }

    /**
     * Получить поток входящих сообщений для конкретного чата
     */
    public LiveData<MessageDTO> getMessageStream(int chatId) {
        return webSocketService.subscribeToChat(chatId);
    }

    /**
     * Завершение соединения с WebSocket (по завершению UI-жизни)
     */
    public void disconnectWebSocket() {
        webSocketService.disconnect();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // disconnectWebSocket();
    }

    public void deleteChat(int chatId) {
        List<ChatDTO> currentList = chats.getValue();

        if (currentList == null || currentList.isEmpty()) return;

        List<ChatDTO> updatedList = new ArrayList<>(currentList);
        updatedList.removeIf(c -> c.getId().equals(chatId));

        chats.setValue(updatedList);
    }

    /**
     * Отправка события на создание группового чата
     */
    public void createChat(ChatDTO chat, ResultCallback<Void> callback) {
        webSocketService.sendCreateGroupChat(chat, callback);
    }

    /**
     * Отправка события на обновление чата (название, аватар и т.п.)
     */
    public void updateChat(ChatPayloadInfo updateInfo, ResultCallback<Void> callback) {
        webSocketService.sendUpdateGroupChat(updateInfo, callback);
    }

    /**
     * Отправка события на удаление чата
     */
    public void deleteChatFromServer(int chatId, int userId, ResultCallback<Void> callback) {
        webSocketService.sendDeleteGroupChat(chatId, userId, callback);
        deleteChat(chatId);
    }

    public void checkConnection() {
        webSocketService.checkConnection();
    }
}
