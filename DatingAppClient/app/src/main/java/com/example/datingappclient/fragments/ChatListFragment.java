package com.example.datingappclient.fragments;

import android.opengl.Visibility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.chatsList.ChatsAdapter;
import com.example.datingappclient.retrofit.RetrofitService;
import com.example.datingappclient.retrofit.ServerAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {

    private int userID;
    RecyclerView recyclerView;

    public ChatListFragment(int userID) {
        this.userID = userID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View activityView = inflater.inflate(R.layout.fragment_chatlist, container, false);
        recyclerView = activityView.findViewById(R.id.chatsList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activityView.getContext()));

        RetrofitService retrofitService = new RetrofitService();
        ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

        serverAPI.getChats(userID).enqueue(new Callback<List<Object[]>>() {
            @Override
            public void onResponse(Call<List<Object[]>> call, Response<List<Object[]>> response) {
                populateListView(response.body());
            }

            @Override
            public void onFailure(Call<List<Object[]>> call, Throwable throwable) {

            }
        });

        return activityView;
    }

    private void populateListView(List<Object[]> chats) {
        TextView noChats = getView().findViewById(R.id.noChats_label);
        if (chats.get(0)[1] == null) {
            ChatsAdapter chatsAdapter = new ChatsAdapter(chats, userID, this, getActivity());
            recyclerView.setAdapter(chatsAdapter);
            noChats.setVisibility(View.GONE);
        } else {
            noChats.setVisibility(View.VISIBLE);
        }
    }
}
