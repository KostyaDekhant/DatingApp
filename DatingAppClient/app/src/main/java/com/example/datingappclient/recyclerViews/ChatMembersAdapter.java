package com.example.datingappclient.recyclerViews;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.GroupChatDTO;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.ChatMembersViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

@Getter
public class ChatMembersAdapter extends ListAdapter<ChatMemberDTO, ChatMembersAdapter.ViewHolder> {

    private final Set<Integer> selectedUserIds = new HashSet<>();
    private final List<ChatMemberDTO> allMembers = new ArrayList<>();

    public ChatMembersAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMemberDTO chatMemberDTO = getItem(position);
        holder.nameTextView.setText(chatMemberDTO.getUsername());
        //holder.statusTextView.setText(chatMemberDTO.get());

        // аватар
        if (chatMemberDTO.getAvatar() != null) {
            Bitmap bmp = ImageUtils.convertPrimitiveByteToBitmap(chatMemberDTO.getAvatar());
            holder.avatarImageView.setImageBitmap(ImageUtils.getCroppedBitmap(bmp));
        }

        // установить состояние выбора
        boolean isSelected = selectedUserIds.contains(chatMemberDTO.getUserId());
        holder.container.setSelected(isSelected);
        holder.checkmark.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        holder.avatarBorder.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        // клик по элементу
        holder.itemView.setOnClickListener(v -> {
            if (isSelected) {
                selectedUserIds.remove(chatMemberDTO.getUserId());
            } else {
                selectedUserIds.add(chatMemberDTO.getUserId());
            }
            notifyItemChanged(position);
        });
    }

    public void setFullList(List<ChatMemberDTO> members) {
        allMembers.clear();
        allMembers.addAll(members);
        submitList(new ArrayList<>(members)); // копия для мутабельности
    }

    public void filter(String query) {
        List<ChatMemberDTO> filtered = new java.util.ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filtered.addAll(allMembers);
        } else {
            String lower = query.toLowerCase();
            for (ChatMemberDTO member : allMembers) {
                if (member.getUsername() != null && member.getUsername().toLowerCase().contains(lower)) {
                    filtered.add(member);
                }
            }
        }
        submitList(filtered);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarImageView, checkmark;
        TextView nameTextView, statusTextView;
        View container, avatarBorder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            checkmark = itemView.findViewById(R.id.checkmark);
            avatarBorder = itemView.findViewById(R.id.avatarBorder);
        }
    }

    static final DiffUtil.ItemCallback<ChatMemberDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMemberDTO oldItem, @NonNull ChatMemberDTO newItem) {
            return oldItem.getUserId().equals(newItem.getUserId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMemberDTO oldItem, @NonNull ChatMemberDTO newItem) {
            return oldItem.equals(newItem); // должен быть переопределён equals
        }
    };

}
