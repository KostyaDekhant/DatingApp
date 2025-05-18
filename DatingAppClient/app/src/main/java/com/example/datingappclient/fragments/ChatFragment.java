package com.example.datingappclient.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.recyclerViews.messageList.MessagesAdapter;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.DialogViewModel;
import com.example.datingappclient.viewmodels.factory.DialogViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ChatFragment extends Fragment {
    /* === Repository === */

    /* === Android Objects === */
    private View activityView;
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;

    /* === Other === */
    Integer userId;
    private ChatDTO chat;

    /* === View Models === */
    private DialogViewModel viewModel;

    private ChatFragment() {}

    public static ChatFragment newInstance (Integer userId, ChatDTO chat) {
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.chat = chat;
        chatFragment.userId = userId;
        return chatFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_dialog, container, false);

        setupMessageRecyclerView();

        AuthResponse authResponse = getAuthResponse();

        // Инициализируем ViewModel с кастомной фабрикой
        viewModel = new ViewModelProvider(this, new DialogViewModelFactory(authResponse.getToken(), chat.getId())).get(DialogViewModel.class);

        renderUsername();
        renderChatImage();
        setupReturnButton();
        setupSendButton();
        openChatEdit();

        viewModelSubscribe();

        return activityView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.disconnect(); // Закрываем соединение
        ChatDTO.selectedChat = null;
    }

    private void openChatEdit() {
        View field = activityView.findViewById(R.id.userinfo);
        field.setOnClickListener((view -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, ChatEditFragment.newInstance(chat))
                    .addToBackStack(null)
                    .commit();
        }));
    }

    private void viewModelSubscribe() {
        // Подписка на историю сообщений (запросятся при подписке)
        viewModel.getHistoryMessages().observe(this.getViewLifecycleOwner(), messages -> {
            messagesAdapter = new MessagesAdapter(messages, userId, chat.getChatInfo());
            messagesRecyclerView.setAdapter(messagesAdapter);
            messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
        });

        // Подписка на новые входящие сообщения
        viewModel.getNewMessage().observe(this.getViewLifecycleOwner(), message -> {
            messagesAdapter.addMessage(message);
            messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
        });
    }

    private void setupMessageRecyclerView() {
        messagesRecyclerView = activityView.findViewById(R.id.messages_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setupSendButton() {
        String logTag = Constants.GLOBAL_LOG_TAG + "CLICK SEND MESS";
        MaterialButton sendButton = activityView.findViewById(R.id.sendmess_button);
        sendButton.setOnClickListener(view -> {
            TextInputEditText messageInput = activityView.findViewById(R.id.message_inputEdit);
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                LocalDateTime dateTime = LocalDateTime.now(ZoneId.systemDefault());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                Timestamp timestamp = Timestamp.valueOf(dateTime.format(formatter));
                MessageDTO messageDTO = new MessageDTO(
                        message,
                        timestamp,
                        userId,
                        chat.getId());
                viewModel.sendMessage(messageDTO);
                messageInput.setText("");
                Log.i(logTag, messageDTO.toString());
            }
        });
    }

    private void renderUsername() {
        TextView usernameLabel = activityView.findViewById(R.id.username_label);
        usernameLabel.setText(chat.getName());
    }

    private void setupReturnButton() {
        MaterialButton returnButton = activityView.findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> getActivity().finish());
    }

    private void renderChatImage() {
        ImageView profileImage = activityView.findViewById(R.id.profile_image);
        if (chat.getImage() != null) {
            Bitmap croppedImage = ImageUtils.getCroppedBitmap(ImageUtils.convertPrimitiveByteToBitmap(chat.getImage()));
            profileImage.setImageBitmap(croppedImage);
            profileImage.setPadding(0, 0, 0, 0);
        }
    }

    private AuthResponse getAuthResponse() {
        SharedPreferences prefs = requireContext().getSharedPreferences("auth", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        String token = prefs.getString("token", null);
        return new AuthResponse(token, userId);
    }
}
