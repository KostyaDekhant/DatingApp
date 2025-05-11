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
import com.example.datingappclient.model.ChatDTO;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatsViewModel;

public class ChatsAdapter extends ListAdapter<ChatDTO, ChatsHolder> {

    public interface OnChatClickListener {
        void onChatClicked(ChatDTO chat, byte[] imageBytes);
    }

    private final OnChatClickListener chatClickListener;
    private final ChatsViewModel viewModel;
    private final int senderId;
    private final LifecycleOwner lifecycleOwner;

    public ChatsAdapter(OnChatClickListener listener, ChatsViewModel viewModel, int senderId, LifecycleOwner owner) {
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
        ChatDTO chat = getItem(position);

        holder.username.setText(chat.getPartnerName());
        holder.setReceiverID(chat.getChatId());
        // render init last message
        if (chat.getLastMessage() != null) {
            String prefix = (chat.getPartnerId() == senderId) ? "Вы: " : "";
            holder.lastMessage.setText(prefix + chat.getLastMessage());
        }

        // Установить изображение, если оно есть
        if (chat.getAvatar() != null) {
            Bitmap bitmap = ImageUtils.convertPrimitiveByteToBitmap(chat.getAvatar());
            Bitmap cropped = ImageUtils.getCroppedBitmap(bitmap);
            holder.setByteImage(chat.getAvatar());
            holder.profileImage.setImageBitmap(cropped);
            holder.profileImage.setPadding(0, 0, 0, 0); // Убираем паддинги, чтобы не было "обводки"
        }

        // Подписка на LiveData для сообщений этого чата
        viewModel.getMessageStream(chat.getChatId())
                .observe(lifecycleOwner, message -> {
                    if (message != null) {
                        String prefix = (message.getPk_user() == senderId) ? "Вы: " : "";
                        holder.lastMessage.setText(prefix + message.getMessage());
                    }
                });

        holder.itemView.setOnClickListener(view ->
                chatClickListener.onChatClicked(chat, holder.getByteImage())
        );
    }

    private static final DiffUtil.ItemCallback<ChatDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatDTO oldItem, @NonNull ChatDTO newItem) {
            return oldItem.getChatId().equals(newItem.getChatId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatDTO oldItem, @NonNull ChatDTO newItem) {
            return oldItem.equals(newItem); // должен быть переопределён equals
        }
    };
}
