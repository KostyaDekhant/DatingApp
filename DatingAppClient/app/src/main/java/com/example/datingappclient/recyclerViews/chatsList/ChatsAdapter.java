package com.example.datingappclient.recyclerViews.chatsList;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.datingappclient.R;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatsViewModel;

public class ChatsAdapter extends ListAdapter<ChatDTO, ChatsHolder> {

    public interface OnChatClickListener {
        void onChatClicked(ChatDTO chat);
    }

    private final ChatsAdapter.OnChatClickListener chatClickListener;
    private final ChatsViewModel viewModel;
    private final int senderId;
    private final LifecycleOwner lifecycleOwner;

    public ChatsAdapter(ChatsAdapter.OnChatClickListener listener, ChatsViewModel viewModel, int senderId, LifecycleOwner owner) {
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
        Log.d("ADAPTER", "Отображается чат: " + chat.getName() + " [" + position + "]");
        Log.d("RV_ITEM_HEIGHT", "itemView height: " + holder.itemView.getHeight());

        holder.username.setText(chat.getName());
        holder.setReceiverID(chat.getId());
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
        viewModel.getMessageStream(chat.getId())
                .observe(lifecycleOwner, message -> {
                    if (message != null && chat.getChatInfo() != null && !chat.getChatInfo().getIsGroup()) {
                        String prefix = (message.getSenderId() == senderId) ? "Вы: " : "";
                        holder.lastMessage.setText(prefix + message.getMessage());
                    }
                });

        holder.itemView.setOnClickListener(view ->
                chatClickListener.onChatClicked(chat)
        );
    }

    private static final DiffUtil.ItemCallback<ChatDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatDTO oldItem, @NonNull ChatDTO newItem) {
            return false;
            //return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatDTO oldItem, @NonNull ChatDTO newItem) {
            return false;
            //return oldItem.equals(newItem); // должен быть переопределён equals
        }
    };
}

