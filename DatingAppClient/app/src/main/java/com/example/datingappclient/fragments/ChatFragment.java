package com.example.datingappclient.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.recyclerViews.messageList.MessagesAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.retrofit.repository.MessagesRepository;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.example.datingappclient.viewmodels.DialogViewModel;
import com.example.datingappclient.viewmodels.OnlineStatusViewModel;
import com.example.datingappclient.viewmodels.factory.DialogViewModelFactory;
import com.example.datingappclient.websocket.StompClientService;
import com.example.datingappclient.websocket.controllers.MessagesController;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.Disposable;

public class ChatFragment extends Fragment {
    /* === Repository === */
    private ChatsRepository chatsRepository;
    private MessagesRepository messagesRepository;

    private MessagesController messagesController;

    /* === Android Objects === */
    private View activityView;
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private TextView usernameLabel, lastOnlineView;
    private ImageView profileImage;

    private ConstraintLayout layout;
    private ProgressBar progressBar;
    private ImageView onlineStatusView;

    /* === Other === */
    Integer userId;
    private ChatDTO chat;

    /* === View Models === */
    private DialogViewModel dialogViewModel;
    private ChatsViewModel chatsViewModel;
    private ChatMembersViewModel chatMembersViewModel;

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

        layout = activityView.findViewById(R.id.mainContent);
        progressBar = activityView.findViewById(R.id.progressBar);

        layout.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);

        setupMessageRecyclerView();

        chatsRepository = new ChatsRepository(requireContext());
        messagesRepository = new MessagesRepository(requireContext());

        messagesController = MessagesController.getInstance();

        usernameLabel = activityView.findViewById(R.id.username_label);
        lastOnlineView = activityView.findViewById(R.id.lastOnlineView);
        profileImage = activityView.findViewById(R.id.profile_image);
        profileImage.setTransitionName("profile_photo");

        onlineStatusView = activityView.findViewById(R.id.statusView);

        // Инициализируем ViewModel с кастомной фабрикой
        DatingAppApplication app = DatingAppApplication.getInstance();
        if (app.getDialogViewModel() == null) {
            dialogViewModel = new ViewModelProvider(this, new DialogViewModelFactory(chat.getId(), userId)).get(DialogViewModel.class);
            app.setDialogViewModel(dialogViewModel);
        }
        else
            dialogViewModel = app.getDialogViewModel();

        renderUsername();
        renderChatImage();
        setupReturnButton();
        setupSendButton();
        setupClickOnChat();

        return activityView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupChatsViewModel();
        setupOnlineStatusUpdate();
        subscribeUpdateChatEvent();

        getChatUnreadMessages(this::setupMessageAdapter);

        subscribeToGetReadMessages();
        //getPersonalUnreadMessages(this::sendReadMessages);

    }

    @Override
    public void onResume() {
        super.onResume();
        chatsViewModel.getUnreadMessages(chat.getId(), userId).observe(getViewLifecycleOwner(), this::sendReadMessages);
        layout.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void subscribeToGetReadMessages() {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET READ MESSAGES";
        StompClientService
                .getInstance()
                .getSubscriptionManager()
                .subscribeToReadMessages(chat.getId())
                .observe(getViewLifecycleOwner(), notification -> {
                    if (notification == null) return;

                    Log.i(logTag, "Прочитанные сообщения получены успешно");

                    if (messagesAdapter != null) {
                        messagesAdapter.markMessagesAsRead(notification);
                    } else {
                        Log.w(logTag, "messagesAdapter ещё не инициализирован");
                    }
                });;
    }

    private void setupMessageAdapter(List<MessageDTO> loaded) {
        messagesAdapter = new MessagesAdapter(userId, chat.getChatInfo(), loaded);

        messagesAdapter.setMemberClickListener(member -> {
            Fragment profileFragment = ProfileFragment.getInstance(member.getId());

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, profileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        messagesRecyclerView.setAdapter(messagesAdapter);

        enableAutoScrollOnNewMessage();
        subscribeGetMessage();
    }

    interface UnreadMessagesCallback {
        void onLoaded(List<MessageDTO> messages);
    }
    private void getPersonalUnreadMessages(UnreadMessagesCallback callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET UNREAD";
        messagesRepository.fetchPersonalUnreadMessages(chat.getId(), userId, result -> {
            switch(result.status) {
                case SUCCESS:
                    Log.i(logTag, "Получено " + result.data.size() + " непрочитанных сообщений (мной)!");
                    callback.onLoaded(result.data);
                    break;
                case EMPTY:
                    Log.i(logTag, "Все сообщения прочитаны (мной)!");
                    callback.onLoaded(new ArrayList<>());
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    callback.onLoaded(new ArrayList<>());
                    break;
            }
        });
    }

    private void getChatUnreadMessages(UnreadMessagesCallback callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET UNREAD";
        messagesRepository.fetchChatUnreadMessages(chat.getId(), userId, result -> {
            switch(result.status) {
                case SUCCESS:
                    Log.i(logTag, "Получено " + result.data.size() + " непрочитанных сообщений (другими)!");
                    callback.onLoaded(result.data);
                    break;
                case EMPTY:
                    Log.i(logTag, "Все сообщения прочитаны (другими)!");
                    callback.onLoaded(new ArrayList<>());
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    callback.onLoaded(new ArrayList<>());
                    break;
            }
        });
    }

    private void setupOnlineStatusUpdate() {
        int receiverId = chat.getChatInfo().getPersonalReceiver(userId);
        if (receiverId != 0) {
            // init render
            renderOnline(receiverId);

            DatingAppApplication app = DatingAppApplication.getInstance();
            OnlineStatusViewModel onlineStatusViewModel = app.getOnlineStatusViewModel();

            onlineStatusViewModel.getOnlineStatuses().observe(getViewLifecycleOwner(), map -> renderOnline(receiverId));
        }
        else lastOnlineView.setVisibility(GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatsViewModel.unsubscribeUpdateChat(chat.getId());         // Отписываеся от обновлений чата
        dialogViewModel.clear();
        ChatDTO.selectedChat = null;
    }

    private void renderOnline(int receiverId) {
        boolean isOnline = DatingAppApplication.getInstance().getOnlineStatusViewModel().isUserOnline(receiverId);
        Timestamp lastOnline = DatingAppApplication.getInstance().getOnlineStatusViewModel().getLastOnline(receiverId);
        onlineStatusView.setVisibility(isOnline ? VISIBLE : GONE);

        lastOnlineView.setVisibility(VISIBLE);
        if (lastOnline != null) {
            lastOnlineView.setText("Был(а) в сети: " + DateUtils.formatSmartTime(lastOnline));
            lastOnlineView.setTextColor(getThemeColor(R.attr.textColorFade));
        }
        else {
            lastOnlineView.setText("в сети");
            lastOnlineView.setTextColor(getThemeColor(R.attr.textColorContrast));
        }
    }

    private int getThemeColor(int textColorFade) {
        TypedValue typedValue = new TypedValue();
        Context context = lastOnlineView.getContext();
        Resources.Theme theme = context.getTheme();
        // Укажи нужный атрибут, например R.attr.textColorSecondary
        theme.resolveAttribute(textColorFade, typedValue, true);

        // Получаем цвет из resolved attribute
        @ColorInt int color = ContextCompat.getColor(context, typedValue.resourceId);
        return color;
    }

    private void subscribeUpdateChatEvent() {
        // Подписываемся на измененеия чата, чтобы изменять его во фрагменте
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();
        chatsViewModel.subscribeToUpdateChat(chat.getId(), result1 -> {
            Log.d(Constants.GLOBAL_LOG_TAG + "UPDATE CHAT", "ChatFragment");
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
                        if (chat != null) {
                            chat.setImage(result.data.getImage());
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

    private void sendReadMessage(MessageDTO message) {
        List<Integer> list = new ArrayList<>();
        list.add(message.getId());

        messagesAdapter.addUnreadMessage(message.getId());
        messagesController.sendReadMessages(chat.getId(), userId, list, result -> {});
    }

    private void sendReadMessages(List<MessageDTO> unreadMessages) {
        if (unreadMessages.isEmpty()) return;

        List<Integer> ids = new ArrayList<>();
        for (MessageDTO message : unreadMessages) ids.add(message.getId());
        messagesController.sendReadMessages(chat.getId(), userId, ids, result -> {
            if (result.status == Result.Status.SUCCESS) {
                ChatDTO temp = chat.copy();
                temp.setUnreadCount(0);
                chatsViewModel.updateOrAddChatAndMoveTop(temp);
            }
            else Log.e(Constants.GLOBAL_LOG_TAG + "READ MESSAGES", result.error);
        });

    }

    private void setupChatsViewModel() {
        DatingAppApplication app = DatingAppApplication.getInstance();
        chatsViewModel = app.getChatsViewModel();

        chatMembersViewModel = new ViewModelProvider(this).get(ChatMembersViewModel.class);
        app.setChatMembersViewModel(chatMembersViewModel);
    }

    private void setupClickOnChat() {
        dialogViewModel.unsubscribeFromChat(chat.getId());
        View field = activityView.findViewById(R.id.userinfo);
        if (chat.getPartnerId() == null)
            field.setOnClickListener(view -> openFragment(ChatPropertiesFragment.newInstance(userId, chat)));
        else
            field.setOnClickListener(view -> openFragment(ProfileFragment.getInstance(chat.getPartnerId())));
    }

    private void openFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(profileImage, "profile_photo")
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private boolean isFirstPartLoad;

    private void subscribeGetMessage() {
        dialogViewModel.subscribeToHistory(chat.getId(), userId, getViewLifecycleOwner());

        dialogViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages == null) return;
            Log.d(Constants.GLOBAL_LOG_TAG + "CHAT FRAGMENT", "Observe get new messages! Count: " + messages.size() );
            messagesAdapter.submitList(new ArrayList<>(messages));
            if (!isFirstPartLoad) {
                isFirstPartLoad = true;
                layout.setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
            }
        });

        dialogViewModel.subscribeToChat(chat.getId(), getViewLifecycleOwner(), this::sendReadMessage);
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
        returnButton.setOnClickListener(view -> {

            dialogViewModel.clear();
            requireActivity().finish();

        });
    }

    private void renderChatImage() {
        if (chat.getImage() != null) {
            Bitmap croppedImage = ImageUtils.getCroppedBitmap(ImageUtils.convertPrimitiveByteToBitmap(chat.getImage()));
            profileImage.setImageBitmap(croppedImage);
            profileImage.setPadding(0, 0, 0, 0);
        }
    }
}
