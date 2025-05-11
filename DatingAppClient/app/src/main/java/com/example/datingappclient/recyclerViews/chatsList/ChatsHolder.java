package com.example.datingappclient.recyclerViews.chatsList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatsHolder extends RecyclerView.ViewHolder {

    private Integer receiverID;
    private byte[] byteImage;

    public TextView username, lastMessage;
    public ImageView profileImage;

    public ChatsHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username_label);
        lastMessage = itemView.findViewById(R.id.lastMessage_label);
        profileImage = itemView.findViewById(R.id.profile_image);
    }

}
