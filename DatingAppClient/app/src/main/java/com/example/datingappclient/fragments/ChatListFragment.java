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

    private void setupCreateChatButton() {
        FloatingActionButton fabCreateChat = activityView.findViewById(R.id.createChat);
        fabCreateChat.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, GroupChatMembersFragment.newInstance(user))
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
        getUserChats();
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

    @Override
    public void onResume() {
        super.onResume();
        getUserChats();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatsViewModel.disconnectWebSocket();
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

    private void startChatActivity(ChatDTO chat, byte[] imageBytes) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("senderID", user.getId());
        intent.putExtra("receiverID", chat.getGroupChatId());
        intent.putExtra("username", chat.getName());
        intent.putExtra("image", imageBytes);
        startActivity(intent);
    }
}
