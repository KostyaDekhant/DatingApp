package com.example.datingappclient.recyclerViews.messageList;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.utils.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesHolder> {

    List<MessageDTO> messageList;
    Integer senderID;
    ChatInfoDTO chatInfo ;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public MessagesAdapter(List<MessageDTO> messageList, Integer senderID, ChatInfoDTO chatInfo) {
        this.messageList = messageList;
        this.senderID = senderID;
        this.chatInfo = chatInfo;
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
        MessageDTO message = messageList.get(position);

        holder.textMessage.setText(message.getMessage());

        if (message.getSendtime() != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(message.getSendtime());
            holder.textDateTime.setText(time);
        }

        if (chatInfo != null && chatInfo.getIsGroup() && getItemViewType(position) == VIEW_TYPE_RECEIVED) {
            ChatMemberDTO member = chatInfo.findMemberById(message.getSenderId());
            Bitmap avatar = ImageUtils.convertPrimitiveByteToBitmap(member.getAvatar());
            holder.avatarView.setImageBitmap(ImageUtils.getCroppedBitmap(avatar));
            holder.avatarView.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderId() == senderID) return VIEW_TYPE_SENT;
        else return VIEW_TYPE_RECEIVED;
    }

    public void addMessage(MessageDTO message) {
        this.messageList.add(message);
        notifyDataSetChanged();
    }
}
