package com.example.datingappclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.messageList.MessagesAdapter;
import com.example.datingappclient.model.Message;
import com.example.datingappclient.utils.DateUtils;
import com.example.datingappclient.utils.ImageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.io.IOException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatActivity extends AppCompatActivity {

    Integer sendlerID, receiverID;
    String username;
    byte[] byteImage;

    RecyclerView messagesRecyclerView;


    MessagesAdapter messagesAdapter;

    StompClient stompClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        // Get pk_user from auth activity
        Bundle arguments = getIntent().getExtras();
        sendlerID = arguments.getInt("sendlerID");
        receiverID = arguments.getInt("receiverID");
        username = arguments.getString("username");
        byteImage = arguments.getByteArray("image");

        TextInputEditText editText = findViewById(R.id.message_inputEdit);
        TextView usernameLabel = findViewById(R.id.username_label);
        MaterialButton sendButton = findViewById(R.id.sendmess_button);
        MaterialButton returnButton = findViewById(R.id.return_button);
        messagesRecyclerView = findViewById(R.id.messages_recyclerView);
        ImageView profileImage = findViewById(R.id.profile_image);

        Bitmap croppedImage = ImageUtils.getCroppedBitmap(ImageUtils.convertPrimitiveByteToBitmap(byteImage));
        profileImage.setImageBitmap(croppedImage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);

        usernameLabel.setText(username);

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen) messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class).putExtra("action", "showchats").putExtra("pk_user", sendlerID));
                finish();
            }
        });


        initStompClient();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                String messageText = editText.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    stompClient.send("/app/send", new Message(messageText, DateUtils.getCurrentTimeStamp(), sendlerID, receiverID).toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                Log.d("GOOD SEND", "REST echo send successfully");
                            }, throwable -> {
                                Log.e("BAD SEND", "Error send REST echo", throwable);
                            });

                    editText.setText("");
                    editText.clearFocus();
                } else {
                    Log.d("EMPTY MESSAGE", "Cannot send an empty message");
                }
            }
        });
        stompClient.send("/app/history/" + receiverID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @SuppressLint("CheckResult")
    private void initStompClient() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://26.223.19.56:8080/datingapp");
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

        stompClient.topic("/topic/history/" + receiverID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d("GETMESS", topicMessage.getPayload());
                    populateListView(stringToList(topicMessage.getPayload()));
                });

        stompClient.topic("/topic/messages/" + receiverID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d("GETMESS", topicMessage.getPayload());
                    ObjectMapper mapper = new ObjectMapper();
                    Message message = mapper.readValue(topicMessage.getPayload(), new TypeReference<Message>() {});
                    messagesAdapter.addMessage(message);
                    messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
                });
    }

    private void populateListView(List<Message> messagesList) {
        messagesAdapter = new MessagesAdapter(messagesList, sendlerID);
        messagesRecyclerView.setAdapter(messagesAdapter);
        messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
    }

    public List<Message> stringToList(String json) {
        ObjectMapper mapper = new ObjectMapper();
        List<Message> messages = null;
        try {
            messages = mapper.readValue(json, new TypeReference<List<Message>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }
}