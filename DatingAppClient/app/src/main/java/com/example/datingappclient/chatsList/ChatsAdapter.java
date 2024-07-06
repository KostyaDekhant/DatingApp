package com.example.datingappclient.chatsList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.fragments.ChatFragment;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsHolder> {

    private List<Object[]> chats;
    private int sendlerID;
    private Fragment fragment;

    public ChatsAdapter(List<Object[]> chats, int sendlerID, Fragment fragment) {
        this.chats = chats;
        this.sendlerID = sendlerID;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_item, parent, false);
        return new ChatsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsHolder holder, int position) {
        String username = (String) chats.get(position)[0];

        Double tempUserID = (Double) chats.get(position)[1];
        Integer userID = tempUserID.intValue();
        holder.setReceiverID(userID);

        holder.username.setText(username);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment(sendlerID, holder.getReceiverID(), username)).commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return chats.size();
    }
}
