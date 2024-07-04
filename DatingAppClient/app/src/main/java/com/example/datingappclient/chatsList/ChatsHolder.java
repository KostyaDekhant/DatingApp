package com.example.datingappclient.chatsList;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;

public class ChatsHolder extends RecyclerView.ViewHolder {

    private Double userID;

    TextView username, lastMessage;

    public ChatsHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username_label);
        lastMessage = itemView.findViewById(R.id.lastMessage_label);
    }

    public Double getUserID() {
        return userID;
    }

    public void setUserID(Double userID) {
        this.userID = userID;
    }
}
