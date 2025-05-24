package com.example.datingappclient.viewmodels.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.datingappclient.viewmodels.DialogViewModel;

public class DialogViewModelFactory implements ViewModelProvider.Factory {
    private final String token;
    private final int chatId;
    private final int userId;

    public DialogViewModelFactory(String token, int chatId, int userId) {
        this.token = token;
        this.chatId = chatId;
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DialogViewModel(token, chatId, userId);
    }
}
