package com.example.datingappclient.messageList;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.model.Message;
import com.example.datingappclient.R;
import com.example.datingappclient.model.User;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesHolder> {

    List<Message> messageList;
    Integer sendlerID;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public MessagesAdapter(List<Message> messageList, Integer sendlerID) {
        this.messageList = messageList;
        this.sendlerID = sendlerID;
    }

    @NonNull
    @Override
    public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        if (viewType == VIEW_TYPE_RECEIVED)
            layout = R.layout.item_received_message;
        else
            layout = R.layout.item_sent_message;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesHolder holder, int position) {
        String textMessage = messageList.get(position).getMessage();
        String textDateTime = messageList.get(position).getTime();

        holder.textMessage.setText(textMessage);
        holder.textDateTime.setText(textDateTime.substring(11, 16));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getPk_user() == sendlerID) return VIEW_TYPE_SENT;
        else return VIEW_TYPE_RECEIVED;
    }
}
