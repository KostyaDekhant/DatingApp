package com.example.datingappclient.recyclerViews.chatsList;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.viewmodels.ChatsViewModel;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.Disposable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatsHolder extends RecyclerView.ViewHolder {

    private Integer receiverID;
    private byte[] byteImage;

    public TextView username, lastMessage;
    public ImageView profileImage;

    private Disposable disposable;

    public ChatsHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username_label);
        lastMessage = itemView.findViewById(R.id.lastMessage_label);
        profileImage = itemView.findViewById(R.id.profile_image);
    }

    public void subcribeToUpdateChat(int chatId, ChatsViewModel viewModel, ChatsRepository chatsRepository, int senderId) {
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();
        disposable = viewModel.subscribeToUpdateChat(chatId, result1 -> {
            chatsRepository.fetchChat(chatId, senderId, result -> {
                viewModel.updateOrAddChat(result.data);

                Disposable d = disposableRef.get();
                if (d != null && !d.isDisposed()) {
                    d.dispose();
                }
            });
        });
    }

    public void unsubscribeUpdate() {
        if (disposable != null) disposable.dispose();
    }

}
