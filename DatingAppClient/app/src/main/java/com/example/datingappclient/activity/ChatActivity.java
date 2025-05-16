package com.example.datingappclient.activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.recyclerViews.messageList.MessagesAdapter;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.DialogViewModel;
import com.example.datingappclient.viewmodels.factory.DialogViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Timestamp;
import java.time.Instant;

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

        setupMessageRecyclerView();

        AuthResponse authResponse = getAuthResponse();

        // Инициализируем ViewModel с кастомной фабрикой
        viewModel = new ViewModelProvider(this, new DialogViewModelFactory(authResponse.getToken(), chat.getGroupChatId())).get(DialogViewModel.class);

        renderUsername();
        renderProfileImage();
        setupReturnButton();
        setupSendButton();

        viewModelSubscribe();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.disconnect(); // Закрываем соединение
        ChatDTO.selectedChat = null;
    }

    private void viewModelSubscribe() {
        // Подписка на историю сообщений (запросятся при подписке)
        viewModel.getHistoryMessages().observe(this, messages -> {
            messagesAdapter = new MessagesAdapter(messages, userId, chat.getGroupChatInfo());
            messagesRecyclerView.setAdapter(messagesAdapter);
            messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
        });

        // Подписка на новые входящие сообщения
        viewModel.getNewMessage().observe(this, message -> {
            messagesAdapter.addMessage(message);
            messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
        });
    }

    private void setupMessageRecyclerView() {
        messagesRecyclerView = findViewById(R.id.messages_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setupSendButton() {
        TextInputEditText messageInput = findViewById(R.id.message_inputEdit);
        MaterialButton sendButton = findViewById(R.id.sendmess_button);
        sendButton.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                MessageDTO messageDTO = new MessageDTO(
                        message,
                        (Timestamp) Timestamp.from(Instant.now()),
                        userId,
                        chat.getGroupChatId());
                viewModel.sendMessage(messageDTO);
                messageInput.setText("");
            }
        });
    }

    private void renderUsername() {
        TextView usernameLabel = findViewById(R.id.username_label);
        usernameLabel.setText(chat.getName());
    }

    private void setupReturnButton() {
        MaterialButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> {
            finish();
        });
    }

    private void renderProfileImage() {
        ImageView profileImage = findViewById(R.id.profile_image);
        if (chat.getImage() != null) {
            Bitmap croppedImage = ImageUtils.getCroppedBitmap(ImageUtils.convertPrimitiveByteToBitmap(chat.getImage()));
            profileImage.setImageBitmap(croppedImage);
            profileImage.setPadding(0, 0, 0, 0);
        }
    }

    private void setChat() {
        // Get args from activity
        Bundle arguments = getIntent().getExtras();
        chat = ChatDTO.selectedChat;
        userId = arguments.getInt("userId");
    }

    private AuthResponse getAuthResponse() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        String token = prefs.getString("token", null);
        return new AuthResponse(token, userId);
    }
}