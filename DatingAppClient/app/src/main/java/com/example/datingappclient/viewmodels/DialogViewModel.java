package com.example.datingappclient.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.websocket.ChatWebSocketService;

import java.util.List;

public class DialogViewModel extends ViewModel {
    private final MutableLiveData<MessageDTO> newMessage = new MutableLiveData<>();
    private final MutableLiveData<List<MessageDTO>> history = new MutableLiveData<>();
    private final ChatWebSocketService webSocketService;

    public DialogViewModel(String token, int chatId) {
        webSocketService = new ChatWebSocketService(token);
        webSocketService.subscribeToChat(chatId).observeForever(newMessage::postValue);
        webSocketService.getHistory(chatId, history);
    }

    public LiveData<MessageDTO> getNewMessage() {
        return newMessage;
    }

    public LiveData<List<MessageDTO>> getHistoryMessages() {
        return history;
    }

    public void sendMessage(MessageDTO message) {
        webSocketService.sendMessage(message);
    }

    public void disconnect() {
        webSocketService.disconnect();
    }
}
