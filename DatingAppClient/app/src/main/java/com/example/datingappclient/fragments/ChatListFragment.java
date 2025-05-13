package com.example.datingappclient.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.activity.ChatActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.GroupChatDTO;
import com.example.datingappclient.recyclerViews.chatsList.ChatsAdapter;
import com.example.datingappclient.recyclerViews.chatsList.GroupChatsAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.retrofit.repository.GroupChatsRepository;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.example.datingappclient.viewmodels.GroupChatsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatListFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;
    private GroupChatsRepository groupChatsRepository;

    /* === Android Objects === */
    private View activityView;
    private RecyclerView recyclerView;

    /* === Other === */
    private int userId;

    private ChatsViewModel viewModel;
    private ChatsAdapter adapter;

    private GroupChatsViewModel groupChatsViewModel;
    private GroupChatsAdapter groupChatsAdapter;

    public ChatListFragment() {

    }

    public static ChatListFragment newInstance(int userId) {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_chatlist, container, false);

        if (getArguments() != null) {
            userId = getArguments().getInt("userId");
        }

        setupRepository();
        setupCreateChatButton();

        recyclerView = activityView.findViewById(R.id.chatsList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activityView.getContext()));

        return activityView;
    }

    private void setupCreateChatButton() {
        FloatingActionButton fabCreateChat = activityView.findViewById(R.id.createChat);
        fabCreateChat.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, GroupChatMembersFragment.newInstance(userId))
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String token = requireContext().getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);

        /*// OLD CHATS
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ChatsViewModel(token);
            }
        }).get(ChatsViewModel.class);

        adapter = new ChatsAdapter((chat, image) -> {
            startChatActivity(chat, image); // 👉 Обработка клика по чату: открыть диалог, передать chat и image
        }, viewModel, userId, getViewLifecycleOwner());

        recyclerView.setAdapter(adapter);

        // 📡 Наблюдение за списком чатов
        viewModel.getChats().observe(getViewLifecycleOwner(), adapter::submitList);

        // Пример начальной загрузки (в реальном коде — через репозиторий)
        getUserChats();*/

        // NEW CHATS

        groupChatsViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new GroupChatsViewModel(token);
            }
        }).get(GroupChatsViewModel.class);

        groupChatsAdapter = new GroupChatsAdapter((chat, image) -> {
            startChatActivity(chat, image); // 👉 Обработка клика по чату: открыть диалог, передать chat и image
        }, groupChatsViewModel, userId, getViewLifecycleOwner());

        recyclerView.setAdapter(groupChatsAdapter);

        groupChatsViewModel.getChats().observe(getViewLifecycleOwner(), groupChatsAdapter::submitList);

        getUserGroupChats();
    }

    @Override
    public void onResume() {
        super.onResume();
        //getUserChats();
        getUserGroupChats();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //viewModel.disconnectWebSocket();
        groupChatsViewModel.disconnectWebSocket();
    }

    private void setupRepository() {
        Context context = requireContext();
        chatsRepository = new ChatsRepository(context);
        groupChatsRepository = new GroupChatsRepository(context);
    }

    private void getUserChats() {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET CHATS";
        chatsRepository.fetchUserChats(userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.d(logTag, "Получено чатов: " + result.data.size());
                    //populateListView(result.data);
                    viewModel.setChats(result.data);
                    break;
                case EMPTY:
                    Log.i(logTag, "Чаты для пользователя " + userId + " отсутствуют!");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

    private void getUserGroupChats() {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET GROUP CHATS";
        groupChatsRepository.fetchUserGroupChats(userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.d(logTag, "Получено чатов: " + result.data.size());
                    groupChatsViewModel.setChats(result.data);
                    break;
                case EMPTY:
                    Log.i(logTag, "Чаты для пользователя " + userId + " отсутствуют!");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

    private void startChatActivity(ChatDTO chat, byte[] imageBytes) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("senderID", userId);
        intent.putExtra("receiverID", chat.getChatId());
        intent.putExtra("username", chat.getPartnerName());
        intent.putExtra("image", imageBytes);
        startActivity(intent);
    }

    private void startChatActivity(GroupChatDTO chat, byte[] imageBytes) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("senderID", userId);
        intent.putExtra("receiverID", chat.getGroupChatId());
        intent.putExtra("username", chat.getName());
        intent.putExtra("image", imageBytes);
        startActivity(intent);
    }
}
