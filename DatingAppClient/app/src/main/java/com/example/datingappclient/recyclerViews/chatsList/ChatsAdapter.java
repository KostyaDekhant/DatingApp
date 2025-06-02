package com.example.datingappclient.recyclerViews.chatsList;

import android.annotation.SuppressLint;
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

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.example.datingappclient.viewmodels.OnlineStatusViewModel;
import com.example.datingappclient.websocket.controllers.MessagesController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatsAdapter extends ListAdapter<ChatDTO, ChatsHolder> {

    public interface OnChatClickListener {
        void onChatClicked(ChatDTO chat, View view);
    }

    private final ChatsAdapter.OnChatClickListener chatClickListener;
    private final ChatsRepository chatsRepository;
    private final ChatsViewModel viewModel;
    private final int senderId;
    private final LifecycleOwner lifecycleOwner;
    private final Context context;
    private final OnlineStatusViewModel onlineStatusViewModel;
    private final MessagesController messagesController = MessagesController.getInstance();

    public ChatsAdapter(ChatsAdapter.OnChatClickListener listener, ChatsViewModel viewModel, int senderId, Context context, LifecycleOwner owner) {
        super(DIFF_CALLBACK);
        this.chatClickListener = listener;
        this.viewModel = viewModel;
        this.senderId = senderId;
        this.context = context;
        chatsRepository = new ChatsRepository(context);
        this.lifecycleOwner = owner;
        onlineStatusViewModel = DatingAppApplication.getInstance().getOnlineStatusViewModel();
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
        holder.setChatId(chat.getId());

        // render init last message
        boolean isGroup = chat.getChatInfo() == null || chat.getChatInfo().getIsGroup();
        holder.setLastMessage(chat.getLastMessage(), isGroup, senderId);

        // Установить изображение, если оно есть
        if (chat.getImage() == null) {
            getChatImage(position, () -> setChatImage(holder, chat.getImage()));
        }
        else {
            setChatImage(holder, chat.getImage());
        }

        // Устновить непрочитанные, если есть
        holder.setMessageCount(chat.getUnreadCount());

        // Подписка на ивент удаления чата
        viewModel.subscribeToDeleteChat(chat.getId());

        // Подписка на ивент изменения чата
        holder.subscribeToUpdateChat(chat.getId(), viewModel, chatsRepository, senderId);

        if (chat.getPartnerId() != null) {
            holder.subscribeToUpdateOnline(chat.getPartnerId());
        }

        holder.itemView.setOnClickListener(view ->
                chatClickListener.onChatClicked(chat, view)
        );

       /* messagesController.subscribeToReadMessages(chat.getId())
                .observe(lifecycleOwner, notification -> {
                    Log.d(Constants.GLOBAL_LOG_TAG + "HOLDER READ MESSAGE",
                            "Сейчас непрочитанных сообщений: " + chat.getUnreadCount() +
                                    "\nПрочитаны сообщения " + notification);

                    if (notification.getUserId() == senderId) {
                        int newUnreadCount = chat.getUnreadCount() - notification.getMessageIds().size();
                        chat.setUnreadCount(newUnreadCount); // чтобы не было < 0
                        holder.setMessageCount(chat.getUnreadCount());
                    }
                });*/

        // Подписка на LiveData для сообщений этого чата
        String logTag = Constants.GLOBAL_LOG_TAG + "GET MESSAGE IN HOLDER";
        //boolean isGroup = chat.getChatInfo() == null || chat.getChatInfo().getIsGroup();
        viewModel.getMessageStream(chat.getId())
                .observe(lifecycleOwner, message -> {
                    if (chat.getLastMessage() != null && chat.getLastMessage().getId() == message.getId()) {
                        return; // дубликат
                    }
                    Log.d(logTag, message.toString() + "\n" + chat.getUnreadCount() + " непрочитанных сообщений в чате!");
                    ChatDTO temp = chat.copy();
                    if (message.getSenderId() != senderId) temp.setUnreadCount(chat.getUnreadCount() + 1);
                    temp.setLastMessage(message);
                    viewModel.updateOrAddChat(temp);

                    holder.setMessageCount(temp.getUnreadCount());
                    holder.setLastMessage(message, isGroup, senderId);
                    //moveChatToTop(temp.getId());
                });
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ChatsHolder holder) {
        super.onViewAttachedToWindow(holder);
        /*ChatDTO chat = getItem(holder.getAdapterPosition());
        if (chat != null)*/


    }

    public void observeOnlineStatus() {
        onlineStatusViewModel.getOnlineStatuses().observe(lifecycleOwner, map -> {
            for (int i = 0; i < getItemCount(); i++) {
                ChatDTO chat = getItem(i);
                // только для личных чатов, где нужен онлайн другого участника
                if ( chat.getPartnerId() != null && map.containsKey( chat.getPartnerId())) {
                    notifyItemChanged(i); // обновить карточку
                }
            }
        });
    }

    public void moveChatToTop(int chatId) {
        List<ChatDTO> currentList = new ArrayList<>(getCurrentList());
        int index = -1;

        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getId() == chatId) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            ChatDTO chat = currentList.remove(index);
            currentList.add(0, chat);
            submitList(currentList); // обновляем адаптер
        }
    }

    @Override
    public void onViewRecycled(@NonNull ChatsHolder holder) {
        super.onViewRecycled(holder);
        // Очистка данных, отмена анимаций, обнуление слушателей и т. д.
        holder.unsubscribeUpdate(holder.getChatId());
    }

    private void setChatImage(ChatsHolder holder, byte[] chatImage) {
        if (chatImage != null) {
            Bitmap bitmap = ImageUtils.convertPrimitiveByteToBitmap(chatImage);
            Bitmap cropped = ImageUtils.getCroppedBitmap(bitmap);
            holder.setByteImage(chatImage);
            holder.profileImage.setImageBitmap(cropped);
            holder.profileImage.setPadding(0, 0, 0, 0); // Убираем паддинги, чтобы не было "обводки"
        }
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
                    //Log.i(logTag, "Получено изображение для чата " + chat.getId());
                    byte[] chatImage = result.data.getImage();
                    chat.setImage(chatImage);
                    break;
                case ERROR:
                    Log.e(logTag, result.error);
                    break;
                case EMPTY:
                    //Log.i(logTag, "Изображение для чата " + chat.getId() + " не найдено");
                    break;
            }
            callback.onImage();
        });
    }

    private static final DiffUtil.ItemCallback<ChatDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatDTO oldItem, @NonNull ChatDTO newItem) {
            //Log.d("EQUALS_ITEMS", oldItem.toString() + "\n" + newItem.toString() + "\n" + oldItem.equals(newItem));
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ChatDTO oldItem, @NonNull ChatDTO newItem) {
            //return false;
            boolean same = oldItem.equals(newItem);
            if (!same) {
                Log.d("DIFF_UTIL", "Чат " + oldItem.getId() + " изменён! Обновляем...");
                Log.d("DIFF_UTIL_CONTENT", oldItem + "\n" + newItem + "\n" + oldItem.equals(newItem));
            }
            return oldItem.equals(newItem); // должен быть переопределён equals
        }
    };
}

