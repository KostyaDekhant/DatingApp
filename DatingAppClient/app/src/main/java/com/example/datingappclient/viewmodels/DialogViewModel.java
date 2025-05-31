package com.example.datingappclient.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.websocket.ChatWebSocketService;

import java.util.ArrayList;
import java.util.List;

public class DialogViewModel extends ViewModel {
    private final ChatWebSocketService webSocketService;
    private final MutableLiveData<List<MessageDTO>> messages = new MutableLiveData<>(new ArrayList<>());


    public DialogViewModel(int chatId, int userId) {
        webSocketService = ChatWebSocketService.getInstance();

        webSocketService.subscribeToChat(chatId).observeForever(message -> {
            List<MessageDTO> current = new ArrayList<>(messages.getValue());
            current.add(message);
            messages.postValue(current);
        });

        webSocketService.getHistory(chatId, userId, messages);
    }

    public LiveData<List<MessageDTO>> getMessages() {
        return messages;
    }
    public void sendMessage(MessageDTO message) {
        webSocketService.sendMessage(message);
    }

    public void disconnect() {
        // webSocketService.disconnect();
    }
}
