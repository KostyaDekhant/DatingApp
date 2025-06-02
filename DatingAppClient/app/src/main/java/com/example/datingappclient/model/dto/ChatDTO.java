package com.example.datingappclient.model.dto;

import com.example.datingappclient.recyclerViews.chatsList.ChatsAdapter;
import com.example.datingappclient.utils.gson.ByteDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "image")
public class ChatDTO {
    @JsonProperty("pk_group_chat")
    @SerializedName("pk_group_chat")
    private Integer id;

    @JsonProperty("name")
    @SerializedName("name")
    private String name;

    @JsonProperty("last_message")
    @SerializedName("last_message")
    private MessageDTO lastMessage;

    @JsonProperty("unread_count")
    @SerializedName("unread_count")
    private Integer unreadCount;

    @JsonProperty("image")
    @SerializedName("image")
    @JsonAdapter(ByteDeserializer.class)
    private byte[] image;

    @JsonProperty("group_chat_info")
    @SerializedName("group_chat_info")
    private ChatInfoDTO chatInfo;

    @JsonProperty("partnerId")
    private Integer partnerId;

    // Используется для передачи выбранного чата ChatListFragment -> ChatActivity
    public static ChatDTO selectedChat;

    public ChatDTO copy() {
        ChatDTO copy = new ChatDTO();
        copy.setId(this.id);
        copy.setName(this.name);
        copy.setLastMessage(this.lastMessage != null ? new MessageDTO(this.lastMessage) : null);
        copy.setUnreadCount(this.unreadCount);
        copy.setImage(this.image != null ? Arrays.copyOf(this.image, this.image.length) : null);
        copy.setChatInfo(this.chatInfo != null ? new ChatInfoDTO(this.chatInfo) : null);
        copy.setPartnerId(this.partnerId);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatDTO chat)) return false;
        return Objects.equals(id, chat.id) &&
                Objects.equals(name, chat.name) &&
                Objects.equals(unreadCount, chat.unreadCount) &&
                Objects.equals(lastMessage, chat.lastMessage) &&
                Objects.equals(chatInfo, chat.chatInfo) &&
                Arrays.equals(image, chat.getImage());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, lastMessage, chatInfo, unreadCount);
        result = 31 * result + java.util.Arrays.hashCode(image); // ← тоже добавляем image
        return result;
    }

}
