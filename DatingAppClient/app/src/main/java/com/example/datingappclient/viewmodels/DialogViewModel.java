package com.example.datingappclient.viewmodels;

import static com.example.datingappclient.retrofit.wrapper.Result.Status.EMPTY;
import static com.example.datingappclient.retrofit.wrapper.Result.Status.ERROR;
import static com.example.datingappclient.retrofit.wrapper.Result.Status.SUCCESS;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.retrofit.repository.MessagesRepository;
import com.example.datingappclient.websocket.ChatWebSocketService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void subscribeToHistory(int chatId, int userId, LifecycleOwner lifecycleOwner) {
        /*webSocketService.subscribeHistory(chatId, userId).observe(lifecycleOwner, history -> {
            Log.i(Constants.GLOBAL_LOG_TAG + "HISTORY IN DVM", "Кол-во сообщений в истории: " + history.size());
            messages.setValue(new ArrayList<>(history));
        });*/
        MessagesRepository messagesRepository = new MessagesRepository(DatingAppApplication.getInstance().getApplicationContext());
        messagesRepository.fetchHistory(chatId, Constants.MESSAGE_LIMIT, 0, result -> {
            switch (result.status) {
                case SUCCESS:
                    List<MessageDTO> history = result.data;
                    Log.i(Constants.GLOBAL_LOG_TAG + "HISTORY HTTP", "Кол-во сообщений в истории: " + history.size());
                    messages.postValue(new ArrayList<>(history));
                    break;
                case EMPTY:
                    Log.i(Constants.GLOBAL_LOG_TAG + "HISTORY HTTP", "История пуста.");
                    messages.postValue(new ArrayList<>()); // Очистим, если нужно
                    break;
                case ERROR:
                    Log.e(Constants.GLOBAL_LOG_TAG + "HISTORY HTTP", "Ошибка: " + result.error);
                    break;
            }
        });
    }

    private final Map<Integer, List<MessageDTO>> historyCache = new HashMap<>();
    private final Map<Integer, Integer> historyOffsets = new HashMap<>();

    public void subscribeToChatHistory(int chatId) {
        List<MessageDTO> chatHistory = historyCache.getOrDefault(chatId, new ArrayList<>());
        Log.d( Constants.GLOBAL_LOG_TAG + "HTTP_HISTORY", "Запрос истории для чата " + chatId + ". Текущий размер кэша - " + chatHistory.size());
        int offset = historyOffsets.getOrDefault(chatId, chatHistory.size());
        fetchHistoryBatch(chatId, offset);
    }

    private void fetchHistoryBatch(int chatId, int offset) {
        MessagesRepository messagesRepository = new MessagesRepository(DatingAppApplication.getInstance().getApplicationContext());
        Log.d(  Constants.GLOBAL_LOG_TAG + "HTTP_HISTORY", "Chat: " + chatId + " limit: " + Constants.MESSAGE_LIMIT + " offset: " + offset);
        messagesRepository.fetchHistory(chatId, Constants.MESSAGE_LIMIT, offset, result -> {
            switch (result.status) {
                case SUCCESS -> {
                    List<MessageDTO> newMessages = result.data;
                    Log.d(Constants.GLOBAL_LOG_TAG + "HTTP_HISTORY", "Загрузка истории: " + newMessages.size());
                    List<MessageDTO> cached = historyCache.getOrDefault(chatId, new ArrayList<>());
                    cached.addAll(0, newMessages); // prepend
                    historyCache.put(chatId, cached);
                    historyOffsets.put(chatId, offset + newMessages.size());

                    messages.postValue(new ArrayList<>(cached));

                    if (newMessages.size() == Constants.MESSAGE_LIMIT) {
                        fetchHistoryBatch(chatId, offset + newMessages.size());
                    }
                }
                case ERROR -> {
                    Log.e(Constants.GLOBAL_LOG_TAG + "HTTP_HISTORY", "Ошибка загрузки: " + result.error);
                    List<MessageDTO> cached = historyCache.getOrDefault(chatId, new ArrayList<>());
                    messages.postValue(new ArrayList<>(cached));
                }
                case EMPTY -> {
                    List<MessageDTO> cached = historyCache.getOrDefault(chatId, new ArrayList<>());
                    Log.i(Constants.GLOBAL_LOG_TAG + "HTTP_HISTORY", "История пуста. Смещение: " + offset + "\nКэш: " + cached.size());
                    messages.postValue(new ArrayList<>(cached));
                }
            }
        });
    }


    public interface OnMessage {
        void onReceived(MessageDTO message);
    }
    public void subscribeToChat(int chatId, LifecycleOwner lifecycleOwner, OnMessage callback)  {
        webSocketService.subscribeToChat(chatId).observe(lifecycleOwner, event -> {
            List<MessageDTO> current = new ArrayList<>(historyCache.getOrDefault(chatId, new ArrayList<>()));
            MessageDTO message = event.getContentIfNotHandled();
            if ( message == null) {
                Log.d(Constants.GLOBAL_LOG_TAG + "GET MESSAGE IN DVM", "Сообщение уже было обработано ранее!");
                return;
            }

            //if (message.getContentIfNotHandled() == null || current.contains(message) || isClosing) return;
            Log.d(Constants.GLOBAL_LOG_TAG + "GET MESSAGE IN DVM", "LiveData сработала в DataViewModel\n" + message);

            callback.onReceived(message);

            current.add(message);
            messages.setValue(current);

            historyCache.put(chatId, current);
            historyOffsets.put(chatId, current.size());


        });
    }


    public void unsubscribeFromChat(int chatId) {
        webSocketService.unsubscribeFromChat(chatId);
    }

    public static boolean isClosing = false;
    public void clear() {
        isClosing = true;
        messages.setValue(new ArrayList<>());
        if (ChatDTO.selectedChat != null) {
            int chatId = ChatDTO.selectedChat.getId();
            historyOffsets.put(chatId, 0);
            historyCache.put(chatId, new ArrayList<>());
        }
    }
}
