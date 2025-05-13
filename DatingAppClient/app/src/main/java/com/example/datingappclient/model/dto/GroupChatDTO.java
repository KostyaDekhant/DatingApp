package com.example.datingappclient.model.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupChatDTO {
    @SerializedName("pk_group_chat")
    private Integer groupChatId;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private byte[] image;

    @SerializedName("group_chat_info")
    private GroupChatInfoDTO groupChatInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupChatDTO chat)) return false;
        return Objects.equals(groupChatId, chat.groupChatId) &&
                Objects.equals(name, chat.name) &&
                Objects.equals(groupChatInfo, chat.groupChatInfo) &&
                Arrays.equals(image, chat.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(groupChatId, name, groupChatInfo);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
