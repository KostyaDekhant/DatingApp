package com.example.datingappclient.viewmodels;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.GroupChatDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.websocket.ChatWebSocketService;

import java.util.List;

public class GroupChatsViewModel extends ViewModel {
    private final MutableLiveData<List<GroupChatDTO>> chats = new MutableLiveData<>();
    private final ChatWebSocketService webSocketService;

    public GroupChatsViewModel(String token) {
        this.webSocketService = new ChatWebSocketService(token);
    }

    /**
     * Получить список всех чатов
     */
    public LiveData<List<GroupChatDTO>> getChats() {
        return chats;
    }

    /**
     * Установить (обновить) список чатов
     */
    public void setChats(List<GroupChatDTO> chatList) {
        chats.setValue(chatList);
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
        disconnectWebSocket();
    }
}
