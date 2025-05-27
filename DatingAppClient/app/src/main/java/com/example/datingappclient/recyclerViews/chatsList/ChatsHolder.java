package com.example.datingappclient.recyclerViews.chatsList;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.viewmodels.ChatsViewModel;

import java.util.Objects;
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
    public ImageView profileImage;

    private Disposable disposable;

    public ChatsHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username_label);
        lastMessage = itemView.findViewById(R.id.lastMessage_label);
        profileImage = itemView.findViewById(R.id.profile_image);
        lastMessageTime = itemView.findViewById(R.id.lastMessage_time);
    }

    public void subcribeToUpdateChat(int chatId, ChatsViewModel viewModel, ChatsRepository chatsRepository, int senderId) {
        String logTag = Constants.GLOBAL_LOG_TAG + "UPDATE CHAT";
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();

        disposable = viewModel.subscribeToUpdateChat(chatId, result1 -> {
            Log.d(logTag, "Ивент обновления чата внутри холдера " + chatId);

            chatsRepository.fetchChat(chatId, senderId, chatResult -> {
                if (chatResult.status != Result.Status.SUCCESS || chatResult.data == null) return;

                ChatDTO chat = chatResult.data;

                chatsRepository.fetchChatAvatar(senderId, chatId, avatarResult -> {
                    if (avatarResult.status == Result.Status.SUCCESS && !avatarResult.data.isEmpty()) {
                        chat.setImage(avatarResult.data.get(0).getImage());
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

    public void unsubscribeUpdate() {
        if (disposable != null) disposable.dispose();
    }

    public void setLastMessage(MessageDTO message, boolean isGroup, int userId) {
        if (message == null) return;

        String prefix = "";
        if (!isGroup && message.getSenderId() == userId) prefix = "Вы: ";
        lastMessage.setText(prefix + message.getMessage());
        lastMessageTime.setText(DateUtils.timestampToHoursMins(message.getSendtime()));
        lastMessageTime.setVisibility(VISIBLE);
    }

}
