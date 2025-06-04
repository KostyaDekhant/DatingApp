package com.example.datingappclient.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.Event;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.retrofit.wrapper.ResultCallback;
import com.example.datingappclient.websocket.ChatWebSocketService;
import com.example.datingappclient.websocket.StompClientService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.Disposable;

public class ChatsViewModel extends ViewModel {
    private final MutableLiveData<List<ChatDTO>> chats = new MutableLiveData<>();
    private final ChatWebSocketService webSocketService;

    private final List<Disposable> updateSubscribes = new ArrayList<>();

    public ChatsViewModel() {
        this.webSocketService = ChatWebSocketService.getInstance();
        initSubscriptions();
    }

    public void clear() {
        chats.setValue(new ArrayList<>());
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


    public Disposable subscribeToUpdateChat(int chatId, ResultCallback<Boolean> callback) {
        Disposable disposable = webSocketService.subscribeToChatUpdatedEvents(chatId, callback);
        updateSubscribes.add(disposable);
        return disposable;
    }

    public void clearUpdateSubscribes() {
        for (Disposable disposable : updateSubscribes) {
            if (disposable != null) disposable.dispose();
        }
    }

    public Disposable getUpdateChatSubscribe;

    public void unsubscribeUpdateChat(int chatId) {
        webSocketService.unsubscribeFromChatUpdatedEvents(chatId);
    }

    public void subscribeToCreateChat(int userId) {
        webSocketService.subscribeToChatCreatedEvents(userId,result -> {});
    }

    public Disposable subscribeToCreateChat(int userId, ResultCallback<Integer> callback) {
        return webSocketService.subscribeToChatCreatedEvents(userId, callback);
    }

    public ChatDTO findChatById(int chatId) {
        List<ChatDTO> currentChats = chats.getValue();
        if (currentChats == null) return null;

        for (ChatDTO chat : currentChats) {
            if (chat.getId() == chatId) return chat;
        }
        return null;
    }

    public void updateOrAddChatAndMoveTop(ChatDTO chat) {
        String logTag = Constants.GLOBAL_LOG_TAG + "UPDATE/ADD CHAT";
        List<ChatDTO> currentList = chats.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        List<ChatDTO> newList = new ArrayList<>();
        boolean updated = false;

        // Сначала добавим обновлённый чат в начало
        newList.add(chat.copy());

        // Потом добавим остальные, кроме обновлённого
        for (ChatDTO c : currentList) {
            if (!Objects.equals(c.getId(), chat.getId())) {
                newList.add(c);
            } else {
                updated = true;
            }
        }

        if (updated) {
            Log.d(logTag, "Обновлён и перемещён чат " + chat.getId());
        } else {
            Log.d(logTag, "Новый чат добавлен! " + chat.getId());
        }

        chats.setValue(newList);
    }

    public void updateOrAddChat(ChatDTO chat) {
        String logTag = Constants.GLOBAL_LOG_TAG + "UPDATE/ADD CHAT";
        List<ChatDTO> currentList = chats.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        List<ChatDTO> newList = new ArrayList<>(currentList);
        boolean updated = false;

        for (int i = 0; i < newList.size(); i++) {
            if (Objects.equals(newList.get(i).getId(), chat.getId())) {
                newList.set(i, chat.copy());
                updated = true;
                break;
            }
        }

        if (!updated) {
            Log.d(logTag, "Новый чат добавлен! " + chat.getId());
            newList.add(0, chat);
        } else {
            Log.d(logTag, "Обновлен существующий чат " + chat.getId());
        }

        chats.setValue(newList);
    }

    private void appendChatIfAbsent(ChatDTO chat) {
        List<ChatDTO> current = chats.getValue();
        if (current == null) current = new ArrayList<>();

        boolean exists = current.stream().anyMatch(c -> c.getId().equals(chat.getId()));
        if (!exists) {
            current.add(chat); // добавляем В КОНЕЦ
            chats.setValue(new ArrayList<>(current)); // новая копия — триггер обновления
        }
    }

    public void moveChatToTop(ChatDTO updatedChat) {
        List<ChatDTO> currentList = chats.getValue();
        if (currentList == null) return;

        List<ChatDTO> newList = new ArrayList<>();
        boolean found = false;

        for (ChatDTO chat : currentList) {
            if (Objects.equals(chat.getId(), updatedChat.getId())) {
                if (!found) {
                    newList.add(updatedChat.copy());
                    found = true;
                }
            } else {
                newList.add(chat.copy());
            }
        }

        if (!found) {
            newList.add(0, updatedChat.copy());
        }

        chats.setValue(newList);
    }

    public void moveChatToTop(int chatId) {
        List<ChatDTO> currentList = chats.getValue();
        if (currentList == null) return;

        List<ChatDTO> tempList = new ArrayList<>(currentList);
        int index = -1;

        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).getId() == chatId) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            ChatDTO chat = tempList.remove(index);
            tempList.add(0, chat);
            setChats(tempList); // обновляем адаптер
        }
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
        //Collections.reverse(newChats);
        for (ChatDTO chat : newChats) {
            appendChatIfAbsent(chat);
        }
        /*List<ChatDTO> currentList = chats.getValue();

        if (currentList == null || currentList.isEmpty()) {
            chats.setValue(new ArrayList<>(newChats));
        } else {
            currentList.addAll(newChats);
            chats.setValue(currentList);
        }*/
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
    public LiveData<Event<MessageDTO>> getMessageStream(int chatId) {
        return webSocketService.subscribeToChat(chatId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // disconnectWebSocket();
    }

    public void deleteChat(int chatId) {
        unsubscribeFromChat(chatId);

        List<ChatDTO> currentList = chats.getValue();

        if (currentList == null || currentList.isEmpty()) return;

        List<ChatDTO> updatedList = new ArrayList<>(currentList);
        updatedList.removeIf(c -> c.getId() == chatId);

        chats.setValue(updatedList);
    }

    /**
     * Отправка события на удаление чата
     */
    public void deleteChatFromServer(int chatId, int userId, ResultCallback<Void> callback) {
        webSocketService.sendDeleteGroupChat(chatId, userId, callback);
        deleteChat(chatId);
    }

    private void unsubscribeFromChat(int chatId) {
        webSocketService.unsubscribeFromChat(chatId);
        webSocketService.unsubscribeFromChatUpdatedEvents(chatId);
        webSocketService.unsubscribeFromChatDeletedEvents(chatId);
    }

    public LiveData<List<MessageDTO>> getUnreadMessages(int chatId, int userId) {
        return StompClientService
                .getInstance()
                .getSubscriptionManager()
                .getPersonalUnreadMessages(chatId, userId);
    }
}
