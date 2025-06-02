package com.example.datingappclient.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.activity.ChatActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.recyclerViews.chatsList.ChatsAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ChatListFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private View activityView;
    private RecyclerView recyclerView;
    private  ConstraintLayout layout;
    private ProgressBar progressBar;

    /* === Other === */
    private UserDTO user;
    private int offset;

    /* === Server exchange === */
    private ChatsViewModel chatsViewModel;
    private ChatsAdapter chatsAdapter;

    public ChatListFragment() {}

    public static ChatListFragment newInstance(UserDTO user) {
        ChatListFragment fragment = new ChatListFragment();
        fragment.user = user;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_chatlist, container, false);

        layout = activityView.findViewById(R.id.mainContent);
        progressBar = activityView.findViewById(R.id.progressBar);

        layout.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);

        setupRepository();
        setupCreateChatButton();
        setupRecyclerView();
        setupViewModel();

        // require recyclerView &  chatsAdapter
        enableAutoScrollOnChatMoved();

        initChats();

        return activityView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setupCreateChatButton() {
        FloatingActionButton fabCreateChat = activityView.findViewById(R.id.createChat);
        fabCreateChat.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, GroupChatMembersFragment.newInstance(user))
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupRecyclerView() {
        recyclerView = activityView.findViewById(R.id.chatsList_recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
    }

    private void enableAutoScrollOnChatMoved() {
        chatsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                if (toPosition == 0 && isAtTopOfChatList()) {
                    recyclerView.post(() -> recyclerView.scrollToPosition(0));
                }
            }
        });
    }

    private boolean isAtTopOfChatList() {
        return ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() == 0;
    }

    private void setupViewModel() {
        DatingAppApplication app = (DatingAppApplication) requireActivity().getApplication();

        if (app.getChatsViewModel() != null) {
            chatsViewModel = app.getChatsViewModel();
        }
        else {
            chatsViewModel = new ViewModelProvider(requireActivity()).get(ChatsViewModel.class);
            app.setChatsViewModel(chatsViewModel);
        }

        chatsAdapter = new ChatsAdapter((chat1, view) -> startChatActivity(chat1), chatsViewModel, user.getId(), requireContext(), getViewLifecycleOwner());
        chatsAdapter.observeOnlineStatus();
        recyclerView.setAdapter(chatsAdapter);

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        chatsViewModel.getChats().observe(getViewLifecycleOwner(), chatList -> {
            if (layoutManager.findFirstVisibleItemPosition() == 0) {
                chatsAdapter.submitList(chatList, () -> recyclerView.scrollToPosition(0));
            } else {
                chatsAdapter.submitList(chatList);
            }
        });

        chatsViewModel.subscribeToCreateChat(user.getId(), result -> {
           if (result.status == Result.Status.SUCCESS) {
               getUserChat(result.data, chat -> chatsViewModel.updateOrAddChat(chat));
           }
        });
    }

    public interface ChatCallback {
        void onLoaded(ChatDTO chat);
    }
    private void getUserChat(int chatId, ChatCallback callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET USER CHAT";
        chatsRepository.fetchChat(chatId, user.getId(), result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Получен чат: " + result.data.toString());
                    callback.onLoaded(result.data);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

    private void setupRepository() {
        Context context = requireContext();
        chatsRepository = new ChatsRepository(context);
    }

    // Предотвращаем повторный вызов загрузки чатов при многократном клике на вкладку списка чатов
    private static boolean isChatsLoading;
    private boolean isFirstPartLoad;
    private void initChats() {
        if (isChatsLoading) return;
        isChatsLoading = true;
        offset = 0;
        chatsViewModel.setChats(new ArrayList<>());
        getUserChats();
    }

    private void getUserChats() {
        int limit = Constants.CHATS_LIMIT;
        String logTag = Constants.GLOBAL_LOG_TAG + "GET CHATS";
        chatsRepository.fetchUserChats(user.getId(), limit, offset, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.d(logTag, "Получено чатов: " + result.data.size());
                    offset += limit;
                    // Если получили кол-во чатов = limit, то запрашиваем еще раз
                    chatsViewModel.addChats(new ArrayList<>(result.data));
                    if (!isFirstPartLoad) {
                        progressBar.setVisibility(GONE);
                        layout.setVisibility(VISIBLE);
                        isFirstPartLoad = true;
                    }
                    if (result.data.size() == limit) getUserChats();
                    else isChatsLoading = false;
                    break;
                case EMPTY:
                    Log.i(logTag, "Чаты для пользователя " + user.getId() + " отсутствуют! offset=" + offset );
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

    public final ActivityResultLauncher<Intent> chatLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    int removedChatId = data.getIntExtra("chatIdToRemove", -1);
                    if (removedChatId != -1) {
                        //chatsViewModel.deleteChat(removedChatId);
                        //chatsViewModel.deleteChatFromServer(removedChatId, user.getId());
                    }
                }
            });

    private void startChatActivity(ChatDTO chat) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("userId", user.getId());
        getChatInfo(chat.getId(), chatInfo -> {
            chat.setChatInfo(chatInfo);
            ChatDTO.selectedChat = chat;
            chatLauncher.launch(intent);
        });
    }

    public interface ChatInfoCallback {
        void onResult(ChatInfoDTO chatInfo);
    }
    private void getChatInfo(int chatId, ChatInfoCallback callback) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET CHAT INFO";
        chatsRepository.fetchChatInfo(chatId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Получена информация о чате " + chatId);
                    callback.onResult(result.data);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    callback.onResult(null);
                    break;
                case EMPTY:
                    Log.i(logTag, "Не найдена информация о чате  " + chatId);
                    callback.onResult(result.data);
                    break;
            }
        });
    }
}
