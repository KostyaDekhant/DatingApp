package com.example.datingappclient.recyclerViews.chatsList;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.utils.ImageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class ChatsAdapter__old extends RecyclerView.Adapter<ChatsHolder> {

    public interface OnChatClickListener {
        void onChatClicked(ChatDTO chat, byte[] imageBytes);
    }

    private final List<ChatDTO> chats;
    private final int senderID;
    private final OnChatClickListener chatClickListener;
    private String token;

    private StompClient stompClient;

    public ChatsAdapter__old(List<ChatDTO> chats, int senderId, String token, OnChatClickListener chatClickListener) {
        this.chats = chats;
        this.senderID = senderId;
        this.chatClickListener = chatClickListener;
        this.token = token;
        initStompClient();
    }

    @NonNull
    @Override
    public ChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_item, parent, false);
        return new ChatsHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ChatsHolder holder, int position) {
        String username = chats.get(position).getPartnerName();
        Integer userId = chats.get(position).getChatId();

        stompClient.topic("/topic/messages/" + userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d("GETMESS", topicMessage.getPayload());
                    ObjectMapper mapper = new ObjectMapper();
                    MessageDTO message = mapper.readValue(topicMessage.getPayload(), new TypeReference<MessageDTO>() {
                    });
                    if (senderID == message.getPk_user())
                        holder.lastMessage.setText("Вы: " + message.getMessage());
                    else holder.lastMessage.setText(message.getMessage());
                });


        String lastMessage = chats.get(position).getLastMessage();
        Integer messageSenderID = chats.get(position).getPartnerId();
        byte[] avatar = chats.get(position).getAvatar();

        if (avatar != null) {
            holder.setByteImage(avatar);
            Bitmap bitmapImage = ImageUtils.convertPrimitiveByteToBitmap(avatar);
            Bitmap croppedImage = ImageUtils.getCroppedBitmap(bitmapImage);
            holder.profileImage.setImageBitmap(croppedImage);
        }

        if (lastMessage != null) {
            if (senderID == messageSenderID) holder.lastMessage.setText("Вы: " + lastMessage);
            else holder.lastMessage.setText(lastMessage);
        }

        holder.setReceiverID(userId);
        holder.username.setText(username);

        holder.itemView.setOnClickListener(view -> {
            chatClickListener.onChatClicked(chats.get(position), holder.getByteImage());
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @SuppressLint("CheckResult")
    private void initStompClient() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + Constants.SERVER_ADDRESS + ":" + Constants.SERVER_PORT + "/datingapp");
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + token));

        stompClient.connect(headers); // ✅ передаём токен

        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("STOMP", "Открыто");
                    break;
                case ERROR:
                    Throwable ex = lifecycleEvent.getException();
                    Log.e("STOMP", "Ошибка подключения", ex);
                    break;
                case CLOSED:
                    Log.d("STOMP", "Закрыто");
                    break;
            }
        }, throwable -> {
            Log.e("STOMP", "FATAL: ошибка в lifecycle подписке", throwable);
        });
    }
}
