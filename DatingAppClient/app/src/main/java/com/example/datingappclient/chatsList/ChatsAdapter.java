package com.example.datingappclient.chatsList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.LayoutInflaterCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsHolder> {

    private List<String> chats;

    public ChatsAdapter(List<String> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_item, parent, false);
        return new ChatsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsHolder holder, int position) {
        String name = chats.get(position);
        holder.username.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), holder.username.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
