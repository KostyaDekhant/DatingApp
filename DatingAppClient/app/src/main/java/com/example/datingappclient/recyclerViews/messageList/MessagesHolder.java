package com.example.datingappclient.recyclerViews.messageList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;

public class MessagesHolder extends RecyclerView.ViewHolder {

    // Время отправки привязано к checkmark
    // Скрывать и отображать checkmark2  !!!

    TextView textMessage, textDateTime;
    ImageView avatarView, readMarkView;

    public MessagesHolder(@NonNull View itemView) {
        super(itemView);
        textMessage = itemView.findViewById(R.id.textMessage);
        textDateTime = itemView.findViewById(R.id.textDateTime);
        avatarView = itemView.findViewById(R.id.avatarImageView);
        readMarkView = itemView.findViewById(R.id.checkmark2);
    }

    public void messageIsUnread(boolean contains) {
        readMarkView.setVisibility(contains ? GONE : VISIBLE);
    }
}
