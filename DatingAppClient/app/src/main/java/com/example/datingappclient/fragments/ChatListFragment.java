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
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.model.dto.UserDTO;
import com.example.datingappclient.recyclerViews.chatsList.ChatsAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatListFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private View activityView;
    private RecyclerView recyclerView;

    /* === Other === */
    private UserDTO user;

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

        setupRepository();
        setupCreateChatButton();

        recyclerView = activityView.findViewById(R.id.chatsList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activityView.getContext()));

        return activityView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
        getUserChats();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatsViewModel.disconnectWebSocket();
    }

    private void setupCreateChatButton() {
        FloatingActionButton fabCreateChat = activityView.findViewById(R.id.createChat);
        fabCreateChat.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, GroupChatMembersFragment.newInstance(user))
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupViewModel() {
        String token = requireContext().getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);

        chatsViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ChatsViewModel(token);
            }
        }).get(ChatsViewModel.class);

        chatsAdapter = new ChatsAdapter(this::startChatActivity, chatsViewModel, user.getId(), getViewLifecycleOwner());

        recyclerView.setAdapter(chatsAdapter);

        chatsViewModel.getChats().observe(getViewLifecycleOwner(), chatsAdapter::submitList);
    }

    private void setupRepository() {
        Context context = requireContext();
        chatsRepository = new ChatsRepository(context);
    }

    private void getUserChats() {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET CHATS";
        chatsRepository.fetchUserChats(user.getId(), result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.d(logTag, "Получено чатов: " + result.data.size());
                    chatsViewModel.setChats(result.data);
                    break;
                case EMPTY:
                    Log.i(logTag, "Чаты для пользователя " + user.getId() + " отсутствуют!");
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
            }
        });
    }

    private void startChatActivity(ChatDTO chat) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("userId", user.getId());
        getChatInfo(chat.getId(), chatInfo -> {
            chat.setChatInfo(chatInfo);
            ChatDTO.selectedChat = chat;
            startActivity(intent);
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
