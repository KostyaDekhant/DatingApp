package com.example.datingappclient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.fragments.ChatFragment;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.viewmodels.ChatsViewModel;

public class ChatActivity extends AppCompatActivity {

    /* === View Models === */

    /* === Android Objects === */

    /* === Other === */
    Integer userId;
    private ChatDTO chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        receiveLogoutSignal();

        setChat();

        DatingAppApplication app = (DatingAppApplication) getApplication();
        ChatsViewModel chatsViewModel = app.getChatsViewModel();

        chatsViewModel.subscribeToDeleteChat(chat.getId(), result -> finish());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ChatFragment.newInstance(userId, chat)).commit();
    }

    private void receiveLogoutSignal() {
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent i = new Intent(ChatActivity.this, AuthActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        }, new IntentFilter("com.example.datingappclient.LOGOUT"));
    }

    private void setChat() {
        Bundle arguments = getIntent().getExtras();
        chat = ChatDTO.selectedChat;
        userId = arguments.getInt("userId");
    }
}