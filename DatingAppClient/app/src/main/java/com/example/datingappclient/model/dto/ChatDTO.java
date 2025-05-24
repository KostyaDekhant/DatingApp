package com.example.datingappclient.model.dto;

import com.example.datingappclient.utils.gson.ByteDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {
    @JsonProperty("pk_group_chat")
    @SerializedName("pk_group_chat")
    private Integer id;

    @JsonProperty("name")
    @SerializedName("name")
    private String name;

    @JsonProperty("last_message")
    @SerializedName("last_message")
    private String lastMessage;

    @JsonProperty("image")
    @SerializedName("image")
    @JsonAdapter(ByteDeserializer.class)
    private byte[] image;

    @JsonProperty("group_chat_info")
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
