package com.example.datingappclient.recyclerViews.messageList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.datingappclient.DatingAppApplication;
import com.example.datingappclient.R;
import com.example.datingappclient.constants.Constants;
import com.example.datingappclient.model.ReadMessageNotification;
import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.model.dto.ChatInfoDTO;
import com.example.datingappclient.model.dto.ChatMemberDTO;
import com.example.datingappclient.model.dto.MessageDTO;
import com.example.datingappclient.recyclerViews.ChatMembersAdapter;
import com.example.datingappclient.retrofit.repository.ImageRepository;
import com.example.datingappclient.retrofit.wrapper.Result;
import com.example.datingappclient.utils.ImageUtils;
import com.example.datingappclient.websocket.controllers.MessagesController;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Setter;

public class MessagesAdapter extends ListAdapter<MessageDTO, MessagesHolder> {

    Integer senderID;
    ChatInfoDTO chatInfo ;
    List<MessageDTO> unreadMessages;
    Set<Integer> unreadMessageIds = new HashSet<>();
    private final Map<Integer, Bitmap> membersImages = new HashMap<>();

    @Setter
    private ChatMembersAdapter.OnMemberClickListener memberClickListener;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public MessagesAdapter(Integer senderID, ChatInfoDTO chatInfo, List<MessageDTO> unreadMessages) {
        super(DIFF_CALLBACK);
        this.senderID = senderID;
        this.chatInfo = chatInfo;

        this.unreadMessages = unreadMessages;
        for (MessageDTO message : unreadMessages) {
            unreadMessageIds.add(message.getId());
        }
    }

    @NonNull
    @Override
    public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == VIEW_TYPE_RECEIVED)
                ? R.layout.item_received_message
                : R.layout.item_sent_message;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesHolder holder, int position) {
        MessageDTO message = getItem(position);

        // Установить текст
        holder.textMessage.setText(message.getMessage());

        // Установить время
        if (message.getSendtime() != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(message.getSendtime());
            holder.textDateTime.setText(time);
        }

        // Установить статус прочитанности
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            holder.readMarkView.setVisibility(GONE);
            holder.messageIsUnread(unreadMessageIds.contains(message.getId()));
        }

        // Установить аватарку отправителя в групповом чате
        setSenderImage(holder, position);
    }

    public interface OnMemberClickListener {
        void onMemberClick(ChatMemberDTO member);
    }

    private void setSenderImage(@NonNull MessagesHolder holder, int position) {
        if (chatInfo != null && chatInfo.getIsGroup() && getItemViewType(position) == VIEW_TYPE_RECEIVED) {
            ChatMemberDTO member = chatInfo.findMemberById(getItem(position).getSenderId());

            if (member == null) return;

            if (membersImages.containsKey(member.getId()) && membersImages.get(member.getId()) != null) {
                holder.avatarView.setImageBitmap(ImageUtils.getCroppedBitmap(membersImages.get(member.getId())));

            }
            else {
                setMemberImage(member.getId(), () -> {
                    holder.avatarView.setImageBitmap(ImageUtils.getCroppedBitmap(membersImages.get(member.getId())));
                }) ;
            }

            holder.avatarView.setOnClickListener(view -> {
                if (memberClickListener != null) {
                    memberClickListener.onMemberClick(member);
                }
            });
        }
        else
            if (holder.avatarView != null) holder.avatarView.setVisibility(GONE);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getSenderId() == senderID ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    public void markMessagesAsRead(ReadMessageNotification notification) {
        String logTag = Constants.GLOBAL_LOG_TAG + "NOTIF IN MESADAP";
        Log.d(logTag, notification.toString());
        if (notification.getUserId() == senderID) return;

        Log.d(logTag, "Прочитано не пользователем. Пытаюсь найти сообщение в адаптере");
        List<Integer> readMessageIds = notification.getMessageIds();
        boolean changed = false;
        for (Integer id : readMessageIds) {
            if (unreadMessageIds.remove(id)) {
                Log.d(logTag, "Прочитано не пользователем. Нашел и удалил из Set'а непрочитанных");
                int index = findIndexById(id);
                if (index != -1) {
                    Log.d(logTag, "Прочитано не пользователем. Нашел в адаптере, обновляю");
                    notifyItemChanged(index);
                    changed = true;
                }
                else
                    Log.e(logTag, "Прочитано не пользователем. Не нашел в адаптере, ошибка обновления");
            }
        }
    }

    private int findIndexById(int id) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).getId() == id) return i;
        }
        return -1;
    }

    interface ImageCallback {
        void onLoad();
    }
    public void setMemberImage(int memberId, ImageCallback callback) {
        ImageRepository imageRepository = new ImageRepository(DatingAppApplication.getInstance().getApplicationContext());
        imageRepository.fetchUserImages(memberId, 1, result -> {
            if (result.status == Result.Status.SUCCESS) {
                Log.d(Constants.GLOBAL_LOG_TAG + "MEMBER IMAGE", "Success");
                List<UserImage> image = ImageUtils.objectListToUserImageList(result.data);
                membersImages.put(memberId, image.get(0).getImage());
                callback.onLoad();
            }
            else Log.e(Constants.GLOBAL_LOG_TAG + "MEMBER IMAGE", "Wrong or empty");
        });
    }

    private static final DiffUtil.ItemCallback<MessageDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull MessageDTO oldItem, @NonNull MessageDTO newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessageDTO oldItem, @NonNull MessageDTO newItem) {
            return oldItem.equals(newItem);
        }
    };

    public void addUnreadMessage(int id) {
        unreadMessageIds.add(id);
    }
}
