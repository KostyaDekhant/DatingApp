package com.example.datingappclient.model.dto;

import com.example.datingappclient.utils.gson.ByteDeserializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatDTO {
    @SerializedName("pk_group_chat")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("last_message")
    private String lastMessage;

    @SerializedName("image")
    @JsonAdapter(ByteDeserializer.class)
    private byte[] image;

    @SerializedName("group_chat_info")
    private ChatInfoDTO chatInfo;

    // Используется для передачи выбранного чата ChatListFragment -> ChatActivity
    public static ChatDTO selectedChat;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatDTO chat)) return false;
        return Objects.equals(id, chat.id) &&
                Objects.equals(name, chat.name) &&
                Objects.equals(lastMessage, chat.lastMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastMessage);
    }

}
