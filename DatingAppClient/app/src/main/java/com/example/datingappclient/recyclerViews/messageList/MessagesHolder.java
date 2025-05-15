package com.example.datingappclient.recyclerViews.messageList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;

public class MessagesHolder extends RecyclerView.ViewHolder {

    TextView textMessage, textDateTime;
    ImageView avatarView;

    public MessagesHolder(@NonNull View itemView) {
        super(itemView);
        textMessage = itemView.findViewById(R.id.textMessage);
        textDateTime = itemView.findViewById(R.id.textDateTime);
        avatarView = itemView.findViewById(R.id.avatarImageView);
    }
}
