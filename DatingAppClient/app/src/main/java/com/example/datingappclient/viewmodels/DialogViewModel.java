package com.example.datingappclient.viewmodels;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.websocket.ChatWebSocketService;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class DialogViewModel extends ViewModel {
    private final ChatWebSocketService webSocketService;
    @Getter
    private final MutableLiveData<List<MessageDTO>> messages = new MutableLiveData<>(new ArrayList<>());

    private final int userId;

    public DialogViewModel(int chatId, int userId) {
        webSocketService = ChatWebSocketService.getInstance();

        this.userId = userId;
    }

    public LiveData<List<MessageDTO>> getMessages() {
        return messages;
    }

    public void sendMessage(MessageDTO message) {
        webSocketService.sendMessage(message);
    }

    public void resetHistoryCache(int chatId) {
        messages.setValue(webSocketService.getCacheHistory(chatId));
    }

    public void getHistory(int chatId, int userId, LifecycleOwner lifecycleOwner) {
        webSocketService.subscribeHistory(chatId, userId).observe(lifecycleOwner, history -> {
            Log.i(Constants.GLOBAL_LOG_TAG + "HISTORY IN DVM", "Кол-во сообщений в истории: " + history.size());
            messages.setValue(new ArrayList<>(history));
        });
        //
        // new Handler(Looper.getMainLooper()).postDelayed(() -> webSocketService.triggerHistoryRequest(chatId, userId), 150);
    }

    public interface OnMessage {
        void onReceived(MessageDTO message);
    }
    public void subscribeToChat(int chatId, LifecycleOwner lifecycleOwner, OnMessage callback)  {
        webSocketService.subscribeToChat(chatId).observe(lifecycleOwner, message -> {
            List<MessageDTO> current = new ArrayList<>(messages.getValue());
            if (current.contains(message)) return;
            Log.d(Constants.GLOBAL_LOG_TAG + "GET MESSAFE IN DVM", message.toString());
            current.add(message);
            messages.setValue(current);
            callback.onReceived(message);
        });

    }

    public void unsubscribeFromChat(int chatId) {
        webSocketService.unsubscribeFromChat(chatId);
    }
}
