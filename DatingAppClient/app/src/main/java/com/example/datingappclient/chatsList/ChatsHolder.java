package com.example.datingappclient.chatsList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;

public class ChatsHolder extends RecyclerView.ViewHolder {

    private Integer receiverID;
    private byte[] byteImage;

    TextView username, lastMessage;
    ImageView profileImage;

    public ChatsHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username_label);
        lastMessage = itemView.findViewById(R.id.lastMessage_label);
        profileImage = itemView.findViewById(R.id.profile_image);
    }

    public Integer getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(Integer receiverID) {
        this.receiverID = receiverID;
    }

    public byte[] getByteImage() {
        return byteImage;
    }

    public void setByteImage(byte[] byteImage) {
        this.byteImage = byteImage;
    }
}
