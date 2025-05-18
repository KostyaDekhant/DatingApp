package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Arrays;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMemberDTO {
    @JsonProperty("username")
    private String username;
    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("avatar")
    private byte[]  avatar;

    public ChatMemberDTO() {
        this.username = "";
        this.userId = -1;
        this.avatar = null;
    }

    public ChatMemberDTO(String username, Integer userId, byte[] avatar) {
        this.username = username;
        this.userId = userId;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "ChatMemberDTO{" +
                "username='" + username + '\'' +
                ", userId=" + userId +
                '}';
    }
}
