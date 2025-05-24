package com.example.datingappclient.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.fragments.ChatFragment;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.recyclerViews.messageList.MessagesAdapter;
import com.example.datingappclient.viewmodels.DialogViewModel;

public class ChatActivity extends AppCompatActivity {

    /* === View Models === */
    private DialogViewModel viewModel;

    /* === Android Objects === */
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;

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