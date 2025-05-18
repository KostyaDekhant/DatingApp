package com.example.datingappclient.recyclerViews.chatsList;

import android.content.Context;
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
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
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
    private final Context context;

    public ChatsAdapter(ChatsAdapter.OnChatClickListener listener, ChatsViewModel viewModel, int senderId, Context context, LifecycleOwner owner) {
        super(DIFF_CALLBACK);
        this.chatClickListener = listener;
        this.viewModel = viewModel;
        this.senderId = senderId;
        this.context = context;
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

        holder.username.setText(chat.getName());
        holder.setReceiverID(chat.getId());
        // render init last message
        holder.lastMessage.setText(chat.getLastMessage());

        // Установить изображение, если оно есть
        getChatImage(position, () -> {
            byte[] chatImage = chat.getImage();
            if (chatImage != null) {
                Bitmap bitmap = ImageUtils.convertPrimitiveByteToBitmap(chatImage);
                Bitmap cropped = ImageUtils.getCroppedBitmap(bitmap);
                holder.setByteImage(chatImage);
                holder.profileImage.setImageBitmap(cropped);
                holder.profileImage.setPadding(0, 0, 0, 0); // Убираем паддинги, чтобы не было "обводки"
            }
        });
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

    interface ChatImageCallback {
        void onImage();
    }
    private void getChatImage(int position, ChatImageCallback callback) {
        ChatDTO chat = getItem(position);
        String logTag = Constants.GLOBAL_LOG_TAG + "GET CHAT AVATAR";
        ChatsRepository chatsRepository = new ChatsRepository(context);
        chatsRepository.fetchChatAvatar(senderId, chat.getId(), result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Получено изображение");
                    chat.setImage(result.data.get(0).getImage());
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    Log.i(logTag, "Изображение для чата" + chat.getId() + "не найдено");
                    break;
            }
            callback.onImage();
        });
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

