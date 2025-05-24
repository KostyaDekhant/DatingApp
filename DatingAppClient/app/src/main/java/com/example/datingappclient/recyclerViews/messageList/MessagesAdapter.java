package com.example.datingappclient.recyclerViews.messageList;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.datingappclient.R;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.utils.ImageUtils;

import java.text.SimpleDateFormat;

public class MessagesAdapter extends ListAdapter<MessageDTO, MessagesHolder> {

    Integer senderID;
    ChatInfoDTO chatInfo ;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public MessagesAdapter(Integer senderID, ChatInfoDTO chatInfo) {
        super(DIFF_CALLBACK);
        this.senderID = senderID;
        this.chatInfo = chatInfo;
    }

    @NonNull
    @Override
    public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == VIEW_TYPE_RECEIVED)
                ? R.layout.item_received_message
                : R.layout.item_sent_message;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesHolder holder, int position) {
        MessageDTO message = getItem(position);

        holder.textMessage.setText(message.getMessage());

        if (message.getSendtime() != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(message.getSendtime());
            holder.textDateTime.setText(time);
        }

        if (chatInfo != null && chatInfo.getIsGroup() && getItemViewType(position) == VIEW_TYPE_RECEIVED) {

            ChatMemberDTO member = chatInfo.findMemberById(message.getSenderId());

            if (member != null && member.getImage() != null) {
                Bitmap avatar = ImageUtils.convertPrimitiveByteToBitmap(member.getImage());
                holder.avatarView.setImageBitmap(ImageUtils.getCroppedBitmap(avatar));
            }
            holder.avatarView.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getSenderId() == senderID ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    private static final DiffUtil.ItemCallback<MessageDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull MessageDTO oldItem, @NonNull MessageDTO newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessageDTO oldItem, @NonNull MessageDTO newItem) {
            return oldItem.equals(newItem);
        }
    };

}
