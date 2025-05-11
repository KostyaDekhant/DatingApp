package com.example.datingappclient.viewmodels.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.datingappclient.viewmodels.DialogViewModel;

public class DialogViewModelFactory implements ViewModelProvider.Factory {
    private final String token;
    private final int chatId;

    public DialogViewModelFactory(String token, int chatId) {
        this.token = token;
        this.chatId = chatId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DialogViewModel(token, chatId);
    }
}
