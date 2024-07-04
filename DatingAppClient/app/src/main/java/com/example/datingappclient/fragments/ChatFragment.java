package com.example.datingappclient.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.datingappclient.Message;
import com.example.datingappclient.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;


public class ChatFragment extends Fragment {

    Double sendlerID;
    Integer reciverID;
    String username;

    TextInputEditText editText;

    public ChatFragment(Double sendlerID, Integer reciverID, String username) {
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
        textView.setText(username + " " + reciverID);

        editText = acticityView.findViewById(R.id.message_inputEdit);

        MaterialButton sendButton = acticityView.findViewById(R.id.sendmess_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                StompClient stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://26.223.19.56:8080/datingapp");
                stompClient.connect();
                stompClient.lifecycle().subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d("TAG", "Stomp connection opened");
                            break;

                        case ERROR:
                            Log.e("TAG", "Error", lifecycleEvent.getException());
                            break;

                        case CLOSED:
                            Log.d("TAG", "Stomp connection closed");
                            break;
                    }
                });
                stompClient.send("/api/send", new Message(editText.getText().toString(), getCurrentTimeStamp(), 24, reciverID).toString());
            }
        });

        return acticityView;
    }

    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date now = new Date();
        return sdfDate.format(now);
    }
}