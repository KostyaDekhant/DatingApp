package com.example.datingappclient.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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

        setChat();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ChatFragment.newInstance(userId, chat)).commit();
    }

    private void setChat() {
        Bundle arguments = getIntent().getExtras();
        chat = ChatDTO.selectedChat;
        userId = arguments.getInt("userId");
    }
}