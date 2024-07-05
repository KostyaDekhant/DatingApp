package com.example.datingappclient.messageList;

import com.example.datingappclient.R;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesHolder extends RecyclerView.ViewHolder {

    TextView textMessage, textDateTime;

    public MessagesHolder(@NonNull View itemView) {
        super(itemView);
        textMessage = itemView.findViewById(R.id.textMessage);
        textDateTime = itemView.findViewById(R.id.textDateTime);
    }
}
