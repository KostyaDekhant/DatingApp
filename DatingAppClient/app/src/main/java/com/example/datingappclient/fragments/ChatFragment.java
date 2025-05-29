package com.example.datingappclient.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.TokenManager;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.AuthResponse;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.recyclerViews.messageList.MessagesAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.example.datingappclient.viewmodels.DialogViewModel;
import com.example.datingappclient.viewmodels.factory.DialogViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.Disposable;

public class ChatFragment extends Fragment {
    /* === Repository === */
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private View activityView;
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private TextView usernameLabel;
    private ImageView profileImage;

    /* === Other === */
    Integer userId;
    private ChatDTO chat;

    /* === View Models === */
    private DialogViewModel dialogViewModel;
    private ChatsViewModel chatsViewModel;
    private ChatMembersViewModel chatMembersViewModel;

    private Disposable disposableUpdate;

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

        chatsRepository = new ChatsRepository(requireContext());

        usernameLabel = activityView.findViewById(R.id.username_label);
        profileImage = activityView.findViewById(R.id.profile_image);

        messagesAdapter = new MessagesAdapter(userId, chat.getChatInfo());
        messagesRecyclerView.setAdapter(messagesAdapter);

        enableAutoScrollOnNewMessage();

        setupChatsViewModel();
        subscribeUpdateChatEvent();

        AuthResponse authResponse = getAuthResponse();

        // Инициализируем ViewModel с кастомной фабрикой
        dialogViewModel = new ViewModelProvider(this, new DialogViewModelFactory(authResponse.getToken(), chat.getId(), userId)).get(DialogViewModel.class);

        renderUsername();
        renderChatImage();
        setupReturnButton();
        setupSendButton();
        openChatEdit();

        subscribeGetMessage();

        return activityView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialogViewModel.disconnect();                               // Закрываем соединение
        if (disposableUpdate != null) disposableUpdate.dispose();   // Отписываеся от обновлений чата
        ChatDTO.selectedChat = null;
    }

    private void subscribeUpdateChatEvent() {
        // Подписываемся на измененеия чата, чтобы изменять его во фрагменте
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();
        disposableUpdate = chatsViewModel.subscribeToUpdateChat(chat.getId(), result1 -> {
            if (chat == null) {
                Disposable d = disposableRef.get();
                if (d != null && !d.isDisposed()) {
                    d.dispose();
                }
                return;
            }

            Toast.makeText(requireContext(), "Чат был обновлен!", Toast.LENGTH_LONG).show();
            String logTag = Constants.GLOBAL_LOG_TAG + "UPDATE CHAT";
            Log.d(logTag, "Сработал триггер обновления чата, запрашиваю информацию!");

            chatsRepository.fetchChatAvatar(userId, chat.getId(), result -> {
                switch (result.status) {
                    case SUCCESS:
                        //chatsViewModel.updateOrAddChat(result.data.get(0));
                        if (chat != null) {
                            chat.setImage(result.data.get(0).getImage());
                            renderChatImage();
                        }
                        break;
                    case ERROR:
                        Log.e(logTag, result.error);
                        break;
                    case EMPTY:
                        Log.i(logTag, "Изображение для чата " + chat.getId() + " не найдено!");
                        break;
                }
            });

            chatsRepository.fetchChat(chat.getId(), userId, result -> {
                switch (result.status) {
                    case SUCCESS:
                        Log.d(logTag, "Информация о чате обновлена!");
                        //chatsViewModel.updateOrAddChat(result.data);
                        if (chat != null) {
                            chat.setName(result.data.getName());
                            renderUsername();
                        }
                        break;
                    case EMPTY:
                        Log.e(logTag, "Чат не найден!");
                        break;
                    case ERROR:
                        Log.e(logTag, result.error);
                        break;
                }

                Disposable d = disposableRef.get();
                if (d != null && !d.isDisposed()) {
                    d.dispose();
                }
            });

            chatsRepository.fetchChatInfo(chat.getId(), result -> {
                switch (result.status) {
                    case SUCCESS:
                        Log.d(logTag, "Получна информация о чате (и учатсники)");
                        if (chat != null) chat.setChatInfo(result.data);
                        chatMembersViewModel.setChatMembers(result.data.getMembers());
                        break;
                    case EMPTY:
                        Log.d(logTag, "Информация о чате не найдена!");
                        break;
                    case ERROR:
                        Log.e(logTag, result.error);
                        break;
                }

            });
        });
    }

    private void setupChatsViewModel() {
        DatingAppApplication app = (DatingAppApplication) requireActivity().getApplication();
        chatsViewModel = app.getChatsViewModel();

        chatMembersViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ChatMembersViewModel();
            }
        }).get(ChatMembersViewModel.class);
        app.setChatMembersViewModel(chatMembersViewModel);
    }

    private void openChatEdit() {
        View field = activityView.findViewById(R.id.userinfo);
        field.setOnClickListener((view -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, ChatPropertiesFragment.newInstance(userId, chat))
                    .addToBackStack(null)
                    .commit();
        }));
    }

    private void subscribeGetMessage() {
        dialogViewModel.getMessages().observe(this.getViewLifecycleOwner(), messages -> messagesAdapter.submitList(new ArrayList<>(messages)));
    }

    private void enableAutoScrollOnNewMessage() {
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
            }
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
                dialogViewModel.sendMessage(messageDTO);
                messageInput.setText("");
                Log.i(logTag, messageDTO.toString());
            }
        });
    }

    private void renderUsername() {
        usernameLabel.setText(chat.getName());
    }

    private void setupReturnButton() {
        MaterialButton returnButton = activityView.findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> requireActivity().finish());
    }

    private void renderChatImage() {
        if (chat.getImage() != null) {
            Bitmap croppedImage = ImageUtils.getCroppedBitmap(ImageUtils.convertPrimitiveByteToBitmap(chat.getImage()));
            profileImage.setImageBitmap(croppedImage);
            profileImage.setPadding(0, 0, 0, 0);
        }
    }

    private AuthResponse getAuthResponse() {
        TokenManager tokenManager = new TokenManager(requireActivity().getApplicationContext());
        String token = tokenManager.getAccessToken();
        int userId = tokenManager.getUserId();
        return new AuthResponse(token, null, userId);
    }
}
