package com.example.datingappclient.recyclerViews;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.dto.ChatDTO;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.retrofit.repository.ChatsRepository;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.viewmodels.OnlineStatusViewModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ChatMembersAdapter extends ListAdapter<ChatMemberDTO, ChatMembersAdapter.ViewHolder> {

    private final Set<Integer> selectedUserIds = new HashSet<>();
    private final List<ChatMemberDTO> allMembers = new ArrayList<>();
    private final Map<Integer, ChatMemberDTO> userMap = new HashMap<>();
    private final ChatsRepository chatsRepository;
    private final Context context;
    private final int userId;
    private final int chatId;

    private final OnlineStatusViewModel onlineStatusViewModel;

    @Setter
    private boolean editMembers;

    @Setter
    private boolean userIsOwner;

    public ChatMembersAdapter(Context context, int userId, int chatId) {
        super(DIFF_CALLBACK);
        this.context = context;
        chatsRepository = new ChatsRepository(context);
        this.userId = userId;
        this.chatId = chatId;
        onlineStatusViewModel = DatingAppApplication.getInstance().getOnlineStatusViewModel();
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
        holder.nameTextView.setText(chatMemberDTO.getName());
        //holder.statusTextView.setText(chatMemberDTO.get());

        // аватар
        if (chatMemberDTO.getImage() != null) {
            Bitmap bmp = ImageUtils.convertPrimitiveByteToBitmap(chatMemberDTO.getImage());
            holder.avatarImageView.setImageBitmap(ImageUtils.getCroppedBitmap(bmp));
        }

        // метка владельца
        if (chatMemberDTO.isChatOwner()) holder.ownerTextView.setVisibility(VISIBLE);

        if (isEditMembers()) {
            // установить состояние выбора
            boolean isSelected = selectedUserIds.contains(chatMemberDTO.getId());
            if (!chatMemberDTO.isAlreadyInChat()) {
                holder.container.setSelected(isSelected);
                holder.checkmark.setVisibility(isSelected ? VISIBLE : View.GONE);
                holder.avatarBorder.setVisibility(isSelected ? VISIBLE : View.GONE);
                // клик по элементу
                holder.itemView.setOnClickListener(v -> {
                    if (isSelected) {
                        selectedUserIds.remove(chatMemberDTO.getId());
                    } else {
                        selectedUserIds.add(chatMemberDTO.getId());
                    }
                    notifyItemChanged(position);
                });
            } else {
                holder.checkmark.setVisibility(VISIBLE);
                holder.checkmark.setImageResource(R.drawable.ic_member_in_chat_circle);
            }
        }
        else if (userIsOwner && !chatMemberDTO.isChatOwner()){
            holder.itemView.setOnLongClickListener(v -> {
                showPopupMenu(v, chatMemberDTO);
                return true;
            });
        }
        if (!isEditMembers())
            holder.statusOnline(chatMemberDTO.getId());
    }

    private void showPopupMenu(View v, ChatMemberDTO chatMemberDTO) {
        PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.END);
        popup.getMenuInflater().inflate(R.menu.chat_membert_context_menu, popup.getMenu());

        // Отображение иконок (необязательно, но красиво)
        try {
            Field mFieldPopup = popup.getClass().getDeclaredField("mPopup");
            mFieldPopup.setAccessible(true);
            Object menuPopupHelper = mFieldPopup.get(popup);
            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MenuItem menuItem = popup.getMenu().findItem(R.id.remove_user);
        SpannableString s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), 0, s.length(), 0);
        menuItem.setTitle(s);

        // Обработка кликов по меню
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.remove_user) {
                removeFromGroup(chatMemberDTO);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void removeFromGroup(ChatMemberDTO chatMember) {
        String logTag = Constants.GLOBAL_LOG_TAG + "REMOVE CHAT MEMBER";
        chatsRepository.removeMemberFromChat(chatMember.getId(), chatId, userId, result -> {
            switch (result.status) {
                case SUCCESS:
                    Log.i(logTag, "Пользователь " + chatMember.getId() + " удален из чата " + chatId);
                    Toast.makeText(context, "Пользователь удален из чата!", Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    Log.e(logTag,result.error);
                    Toast.makeText(context, "Ошибка удаления пользователя из чата!", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    public void setFullList(List<ChatMemberDTO> members) {
        allMembers.clear();
        allMembers.addAll(members);

        userMap.clear();
        for (ChatMemberDTO member : members) {
            userMap.put(member.getId(), member);
        }

        submitList(new ArrayList<>(members)); // копия для мутабельности
    }

    public void filter(String query) {
        List<ChatMemberDTO> filtered = new java.util.ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filtered.addAll(allMembers);
        } else {
            String lower = query.toLowerCase();
            for (ChatMemberDTO member : allMembers) {
                if (member.getName() != null && member.getName().toLowerCase().contains(lower)) {
                    filtered.add(member);
                }
            }
        }
        submitList(filtered);
    }

    public List<ChatMemberDTO> getSelectedMembers() {
        List<ChatMemberDTO> selected = new ArrayList<>();
        for (Integer id : selectedUserIds) {
            ChatMemberDTO member = userMap.get(id);
            if (member != null) {
                selected.add(member);
            }
        }
        return selected;
    }

    public void observeOnlineStatus(LifecycleOwner lifecycleOwner) {
        onlineStatusViewModel.getOnlineStatuses().observe(lifecycleOwner, map -> {
            for (int i = 0; i < getItemCount(); i++) {
                ChatMemberDTO member = getItem(i);
                if (member != null && map.containsKey(member.getId())) {
                    notifyItemChanged(i);
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarImageView, checkmark, statusView;
        TextView nameTextView, statusTextView, ownerTextView;
        View container, avatarBorder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            checkmark = itemView.findViewById(R.id.checkmark);
            avatarBorder = itemView.findViewById(R.id.avatarBorder);
            ownerTextView = itemView.findViewById(R.id.ownerTextView);
            statusView = itemView.findViewById(R.id.statusView);
        }

        public void statusOnline(int userId) {
            boolean isOnline = DatingAppApplication.getInstance().getOnlineStatusViewModel().isUserOnline(userId);
            statusView.setVisibility(isOnline ? VISIBLE : GONE);
        }
    }

    static final DiffUtil.ItemCallback<ChatMemberDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMemberDTO oldItem, @NonNull ChatMemberDTO newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMemberDTO oldItem, @NonNull ChatMemberDTO newItem) {
            return oldItem.equals(newItem); // должен быть переопределён equals
        }
    };

}
