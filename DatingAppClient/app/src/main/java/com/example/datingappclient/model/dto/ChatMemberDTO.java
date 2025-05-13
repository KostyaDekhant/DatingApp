package com.example.datingappclient.model.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMemberDTO {
    @SerializedName("username")
    private String username;
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("avatar")
    private byte[]  avatar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMemberDTO chatMember)) return false;
        return Objects.equals(userId, chatMember.userId) &&
                Objects.equals(username, chatMember.username) &&
                Arrays.equals(avatar, chatMember.avatar);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(username, userId);
        result = 31 * result + Arrays.hashCode(avatar);
        return result;
    }
}
