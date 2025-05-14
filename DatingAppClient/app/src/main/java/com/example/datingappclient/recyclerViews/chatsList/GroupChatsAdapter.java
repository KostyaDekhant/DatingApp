package com.example.datingappclient.recyclerViews.chatsList;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.datingappclient.R;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.GroupChatDTO;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.example.datingappclient.viewmodels.GroupChatsViewModel;

public class GroupChatsAdapter extends ListAdapter<GroupChatDTO, ChatsHolder> {

    public interface OnChatClickListener {
        void onChatClicked(GroupChatDTO chat, byte[] imageBytes);
    }

    private final GroupChatsAdapter.OnChatClickListener chatClickListener;
    private final GroupChatsViewModel viewModel;
    private final int senderId;
    private final LifecycleOwner lifecycleOwner;

    public GroupChatsAdapter(GroupChatsAdapter.OnChatClickListener listener, GroupChatsViewModel viewModel, int senderId, LifecycleOwner owner) {
        super(DIFF_CALLBACK);
        this.chatClickListener = listener;
        this.viewModel = viewModel;
        this.senderId = senderId;
        this.lifecycleOwner = owner;
    }

    @NonNull
    @Override
    public ChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_item, parent, false);
        return new ChatsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsHolder holder, int position) {
        GroupChatDTO chat = getItem(position);

        holder.username.setText(chat.getName());
        holder.setReceiverID(chat.getGroupChatId());
        // render init last message
        holder.lastMessage.setText(chat.getLastMessage());

        // Установить изображение, если оно есть
        byte[] chatImage = chat.getImage();
        if (chatImage != null) {
            Bitmap bitmap = ImageUtils.convertPrimitiveByteToBitmap(chatImage);
            Bitmap cropped = ImageUtils.getCroppedBitmap(bitmap);
            holder.setByteImage(chatImage);
            holder.profileImage.setImageBitmap(cropped);
            holder.profileImage.setPadding(0, 0, 0, 0); // Убираем паддинги, чтобы не было "обводки"
        }

        // Подписка на LiveData для сообщений этого чата
        viewModel.getMessageStream(chat.getGroupChatId())
                .observe(lifecycleOwner, message -> {
                    if (message != null && !chat.getGroupChatInfo().getIsGroup()) {
                        String prefix = (message.getPk_user() == senderId) ? "Вы: " : "";
                        holder.lastMessage.setText(prefix + message.getMessage());
                    }
                });

        holder.itemView.setOnClickListener(view ->
                chatClickListener.onChatClicked(chat, holder.getByteImage())
        );
    }

    private static final DiffUtil.ItemCallback<GroupChatDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull GroupChatDTO oldItem, @NonNull GroupChatDTO newItem) {
            return oldItem.getGroupChatId().equals(newItem.getGroupChatId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull GroupChatDTO oldItem, @NonNull GroupChatDTO newItem) {
            return oldItem.equals(newItem); // должен быть переопределён equals
        }
    };
}

