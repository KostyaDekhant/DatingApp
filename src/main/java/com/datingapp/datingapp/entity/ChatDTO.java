package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatDTO {
    @JsonProperty("partnerName")
    private String  partnerName;
    @JsonProperty("chatId")
    private Integer    chatId;
    @JsonProperty("lastMessage")
    private String  lastMessage;
    @JsonProperty("partnerId")
    private Integer partnerId;
    @JsonProperty("avatar")
    private byte[]  avatar;
}
