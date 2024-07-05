package com.example.datingappclient.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.datingappclient.messageList.MessagesAdapter;
import com.example.datingappclient.model.Message;
import com.example.datingappclient.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatFragment extends Fragment {

    Integer sendlerID;
    Integer reciverID;
    String username;

    StompClient stompClient;
    RecyclerView messagesRecyclerView;

    public ChatFragment(Integer sendlerID, Integer reciverID, String username) {
        // Required empty public constructor
        this.sendlerID = sendlerID;
        this.reciverID = reciverID;
        this.username = username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View acticityView = inflater.inflate(R.layout.fragment_chat, container, false);

        TextView textView = acticityView.findViewById(R.id.username_label);
        textView.setText(username);

        TextInputEditText editText = acticityView.findViewById(R.id.message_inputEdit);
        MaterialButton sendButton = acticityView.findViewById(R.id.sendmess_button);

        initStompClient();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                stompClient.send("/app/send", new Message(editText.getText().toString(), getCurrentTimeStamp(), sendlerID, reciverID).toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                    Log.d("GOOD SEND", "REST echo send successfully");
                }, throwable -> {
                    Log.e("BAD SEND", "Error send REST echo", throwable);
                });

                editText.setText("");
                editText.clearFocus();

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        messagesRecyclerView = acticityView.findViewById(R.id.messages_recyclerView);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(acticityView.getContext()));

        stompClient.send("/app/history/" + reciverID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        return acticityView;
    }

    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date now = new Date();
        return sdfDate.format(now);
    }

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

        stompClient.topic("/topic/history")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
            Log.d("GETMESS", topicMessage.getPayload());
            populateListView(stringToList(topicMessage.getPayload()));
        });

        stompClient.topic("/topic/messages")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
            Log.d("GETMESS", topicMessage.getPayload());
        });
    }

    private void populateListView(List<Message> messagesList) {
        messagesRecyclerView.setAdapter(new MessagesAdapter(messagesList, sendlerID));
    }

    public List<Message> stringToList(String json) {
        ObjectMapper mapper = new ObjectMapper();
        List<Message> messages = null;
        try {
            messages = mapper.readValue(json, new TypeReference<List<Message>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
