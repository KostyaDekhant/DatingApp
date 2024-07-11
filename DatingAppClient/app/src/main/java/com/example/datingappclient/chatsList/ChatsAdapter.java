package com.example.datingappclient.chatsList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.ChatActivity;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.Message;
import com.example.datingappclient.utils.ImageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsHolder> {

    private List<Object[]> chats;
    private int sendlerID;
    private Fragment fragment;
    private Activity activity;

    StompClient stompClient;

    public ChatsAdapter(List<Object[]> chats, int sendlerID, Fragment fragment, Activity activity) {
        this.chats = chats;
        this.sendlerID = sendlerID;
        this.fragment = fragment;
        this.activity = activity;
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
        String username = chats.get(position)[0] == null ? null : chats.get(position)[0].toString();
        Integer userID = chats.get(position)[1] == null ? null : ((Double) chats.get(position)[1]).intValue();

        stompClient.topic("/topic/messages/" + userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d("GETMESS", topicMessage.getPayload());
                    ObjectMapper mapper = new ObjectMapper();
                    Message message = mapper.readValue(topicMessage.getPayload(), new TypeReference<Message>() {
                    });
                    if (sendlerID == message.getPk_user())
                        holder.lastMessage.setText("Вы: " + message.getMessage());
                    else holder.lastMessage.setText(message.getMessage());
                });

        String lastMessage = chats.get(position)[2] == null ? null : chats.get(position)[2].toString();
        Integer messageSendlerID = chats.get(position)[3] == null ? null : ((Double) chats.get(position)[3]).intValue();
        String strImage = chats.get(position)[4] == null ? null : chats.get(position)[4].toString();

        if (strImage != null) {
            byte[] byteImage = Base64.getDecoder().decode(strImage);
            holder.setByteImage(byteImage);

            Bitmap bitmapImage = ImageUtils.convertPrimitiveByteToBitmap(byteImage);
            Bitmap croppedImage = ImageUtils.getCroppedBitmap(bitmapImage);
            holder.profileImage.setImageBitmap(croppedImage);
        }

        if(lastMessage != null) {
            if (sendlerID == messageSendlerID) holder.lastMessage.setText("Вы: " + lastMessage);
            else holder.lastMessage.setText(lastMessage);
        }

        holder.setReceiverID(userID);
        holder.username.setText(username);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ChatActivity.class).putExtra("sendlerID", sendlerID).putExtra("receiverID", holder.getReceiverID()).putExtra("username", username).putExtra("image", holder.getByteImage()));
                activity.finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        return chats.size();
    }

    @SuppressLint("CheckResult")
    private void initStompClient() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + Constants.SERVER_ADDRESS + ":" + Constants.SERVER_PORT + "/datingapp");
        stompClient.connect();

        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("OPEN CONNECTION", "Stomp connection opened");
                    break;

                case ERROR:
                    Log.e("ERROR CONNECTION", "Error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.d("CLOSE CONNECTION", "Stomp connection closed");
                    break;
            }
        });
    }
}
