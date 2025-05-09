package com.example.datingappclient.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.ChatDTO;
import com.example.datingappclient.recyclerViews.chatsList.ChatsAdapter;
import com.example.datingappclient.retrofit.repository.ChatsRepository;

import java.util.List;

public class ChatListFragment extends Fragment {

    /* === Repository === */
    private final ChatsRepository chatsRepository;

    /* === Android Objects === */
    private View activityView;
    private RecyclerView recyclerView;

    /* === Other === */
    private final int userId;

    public ChatListFragment(int userID) {
        this.userId = userID;
        chatsRepository = new ChatsRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_chatlist, container, false);
        recyclerView = activityView.findViewById(R.id.chatsList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activityView.getContext()));

        getUserChats(userId);

        return activityView;
    }

    private void getUserChats(int userId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "GET CHATS";
        chatsRepository.fetchUserChats(userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.d(logTag, "Получено чатов: " + result.data.size());
                    populateListView(result.data);
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
        } else {
            ChatsAdapter chatsAdapter = new ChatsAdapter(chats, userId, getActivity());
            recyclerView.setAdapter(chatsAdapter);
            noChats.setVisibility(View.GONE);
        }
    }
}
