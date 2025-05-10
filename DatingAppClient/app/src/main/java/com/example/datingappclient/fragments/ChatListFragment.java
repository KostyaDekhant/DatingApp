package com.example.datingappclient.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.ChatActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.ChatDTO;
import com.example.datingappclient.recyclerViews.chatsList.ChatsAdapter__old;
import com.example.datingappclient.recyclerViews.chatsList.ChatsAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.viewmodels.ChatsViewModel;

import java.util.List;

public class ChatListFragment extends Fragment {

    /* === Repository === */
    private ChatsRepository chatsRepository;

    /* === Android Objects === */
    private View activityView;
    private RecyclerView recyclerView;

    /* === Other === */
    private int userId;

    private ChatsViewModel viewModel;
    private ChatsAdapter adapter;

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

        recyclerView = activityView.findViewById(R.id.chatsList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activityView.getContext()));

        return activityView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String token = requireContext().getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);
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
        getUserChats(userId);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserChats(userId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.disconnectWebSocket();
    }

    private void setupRepository() {
        chatsRepository = new ChatsRepository(requireContext());
    }

    private void getUserChats(int userId) {
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

    private void populateListView(List<ChatDTO> chats) {
        TextView noChats = activityView.findViewById(R.id.noChats_label);
        if (chats.isEmpty()) {
            noChats.setVisibility(View.VISIBLE);
        }
        else {
            String token = requireContext().getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);
            ChatsAdapter__old chatsAdapter = new ChatsAdapter__old(chats, userId, token, this::startChatActivity);
            recyclerView.setAdapter(chatsAdapter);
            noChats.setVisibility(View.GONE);
        }
    }

    private void startChatActivity(ChatDTO chat, byte[] imageBytes) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("senderID", userId);
        intent.putExtra("receiverID", chat.getChatId());
        intent.putExtra("username", chat.getPartnerName());
        intent.putExtra("image", imageBytes);
        startActivity(intent);
    }
}
