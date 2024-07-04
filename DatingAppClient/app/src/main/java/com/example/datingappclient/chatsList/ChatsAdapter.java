package com.example.datingappclient.chatsList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.fragments.ChatFragment;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsHolder> {

    private List<Object[]> chats;
    private Fragment fragment;

    public ChatsAdapter(List<Object[]> chats, Fragment fragment) {
        this.chats = chats;
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
        Double userID = ((Double) chats.get(position)[1]);
        holder.setUserID(userID);

        holder.username.setText(username);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment(holder.getUserID(), 1, username)).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
