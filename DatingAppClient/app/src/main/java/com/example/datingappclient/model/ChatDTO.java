package com.example.datingappclient.model;

import com.example.datingappclient.utils.gson.ByteDeserializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;

@Getter
public class ChatDTO {
    @SerializedName("partnerName")
    private String partnerName;

    @SerializedName("chatId")
    private Integer chatId;

    @SerializedName("lastMessage")
    private String lastMessage;

    @SerializedName("partnerId")
    private Integer partnerId;

    @SerializedName("avatar")
    @JsonAdapter(ByteDeserializer.class)
    private byte[] avatar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatDTO chat)) return false;
        return Objects.equals(partnerName, chat.partnerName) &&
                Objects.equals(chatId, chat.chatId) &&
                Objects.equals(lastMessage, chat.lastMessage) &&
                Objects.equals(partnerId, chat.partnerId) &&
                Arrays.equals(avatar, chat.avatar);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(partnerName, chatId, lastMessage, partnerId);
        result = 31 * result + Arrays.hashCode(avatar);
        return result;
    }
}
