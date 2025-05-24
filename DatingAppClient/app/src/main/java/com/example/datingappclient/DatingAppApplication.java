package com.example.datingappclient;

import android.app.Application;

import com.example.datingappclient.viewmodels.ChatMembersViewModel;
import com.example.datingappclient.viewmodels.ChatsViewModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatingAppApplication extends Application {
    private ChatsViewModel chatsViewModel;
    private ChatMembersViewModel chatMembersViewModel;
}
