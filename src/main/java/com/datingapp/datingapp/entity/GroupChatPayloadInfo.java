package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupChatPayloadInfo {
    @JsonProperty("chatId")
    private int chatId;
    @JsonProperty("userId")
    private int userId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("image")
    private byte[] image;
}
