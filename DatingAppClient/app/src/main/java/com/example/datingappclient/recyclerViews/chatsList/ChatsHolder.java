package com.example.datingappclient.recyclerViews.chatsList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.viewmodels.ChatsViewModel;
import com.example.datingappclient.websocket.UpdateEventsController;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.Disposable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatsHolder extends RecyclerView.ViewHolder {

    private Integer receiverID;
    private byte[] byteImage;

    public TextView username, lastMessage, lastMessageTime;
    public ImageView profileImage, statusView;

    private ChatsViewModel chatsViewModel;

    private Disposable updateDisposable;

    public ChatsHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username_label);
        lastMessage = itemView.findViewById(R.id.lastMessage_label);
        profileImage = itemView.findViewById(R.id.profile_image);
        lastMessageTime = itemView.findViewById(R.id.lastMessage_time);
        statusView = itemView.findViewById(R.id.statusView);
    }

    public void subscribeToUpdateChat(int chatId, ChatsViewModel viewModel, ChatsRepository chatsRepository, int senderId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "UPDATE CHAT";
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();

        this.chatsViewModel = viewModel;

        UpdateEventsController updateEventsController = new UpdateEventsController();
        updateEventsController.subscribeToChatUpdatedEvents(chatId, result -> {

        });

        updateDisposable = viewModel.subscribeToUpdateChat(chatId, result1 -> {

            Log.d(Constants.GLOBAL_LOG_TAG + "UPDATE CHAT", "ChatsHolder");

            chatsRepository.fetchChat(chatId, senderId, chatResult -> {
                if (chatResult.status != Result.Status.SUCCESS || chatResult.data == null) return;

                ChatDTO chat = chatResult.data;

                chatsRepository.fetchChatAvatar(senderId, chatId, avatarResult -> {
                    if (avatarResult.status == Result.Status.SUCCESS) {
                        chat.setImage(avatarResult.data.getImage());
                    }

                    // Теперь обновляем чат
                    viewModel.updateOrAddChat(chat);

                    // Отписываемся от обновления, если нужно
                    Disposable d = disposableRef.get();
                    if (d != null && !d.isDisposed()) {
                        d.dispose();
                    }
                });
            });
        });
    }

    public void unsubscribeUpdate(int chatId) {
        if (updateDisposable != null) updateDisposable.dispose();
        chatsViewModel.unsubscribeUpdateChat(chatId);
    }

    public void setLastMessage(MessageDTO message, boolean isGroup, int userId) {
        if (message == null) return;

        String prefix = "";
        if (!isGroup && message.getSenderId() == userId) prefix = "Вы: ";
        lastMessage.setText(prefix + message.getMessage());
        lastMessageTime.setText(DateUtils.timestampToHoursMins(message.getSendtime()));
        lastMessageTime.setVisibility(VISIBLE);
    }

    public void subscribeToUpdateOnline(int receiverId) {
        boolean isOnline = DatingAppApplication.getInstance().getOnlineStatusViewModel().isUserOnline(receiverId);
        statusView.setVisibility(isOnline ? VISIBLE : GONE);
    }
}
