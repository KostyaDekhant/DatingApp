package com.example.datingappclient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class ChatsContainer extends Fragment {

    private int userID;
    RecyclerView recyclerView;

    public ChatsContainer(int userID) {
        this.userID = userID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View activityView = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = activityView.findViewById(R.id.chatsList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activityView.getContext()));

        List<String> test = new ArrayList<String>();
        test.add("Georgiy");
        test.add("Sergey");


        RetrofitService retrofitService = new RetrofitService();
        ServerAPI serverAPI = retrofitService.getRetrofit().create(ServerAPI.class);

        serverAPI.getChats(userID).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                populateListView(response.body());
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable throwable) {

            }
        });

        return activityView;
    }

    private void populateListView(List<String> chats) {
        ChatsAdapter chatsAdapter = new ChatsAdapter(chats);
        recyclerView.setAdapter(chatsAdapter);
    }
}
