package com.example.datingappclient.viewmodels.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.datingappclient.viewmodels.DialogViewModel;

public class DialogViewModelFactory implements ViewModelProvider.Factory {
    private final int chatId;
    private final int userId;

    public DialogViewModelFactory(int chatId, int userId) {
        this.chatId = chatId;
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DialogViewModel(chatId, userId);
    }
}
