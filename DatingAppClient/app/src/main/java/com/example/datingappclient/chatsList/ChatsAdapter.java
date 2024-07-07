package com.example.datingappclient.chatsList;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.ChatActivity;
import com.example.datingappclient.R;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsHolder> {

    private List<Object[]> chats;
    private int sendlerID;
    private Fragment fragment;
    private Activity activity;

    public ChatsAdapter(List<Object[]> chats, int sendlerID, Fragment fragment, Activity activity) {
        this.chats = chats;
        this.sendlerID = sendlerID;
        this.fragment = fragment;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_item, parent, false);
        return new ChatsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsHolder holder, int position) {
        String username = (String) chats.get(position)[0];

        Double tempUserID = (Double) chats.get(position)[1];
        Integer userID = tempUserID.intValue();
        holder.setReceiverID(userID);

        holder.username.setText(username);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("sendlerID", sendlerID).putExtra("receiverID", holder.getReceiverID()).putExtra("username", username));
                activity.finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        return chats.size();
    }
}
