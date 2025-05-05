package com.example.datingappclient.model;

import com.example.datingappclient.utils.ByteDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

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
}
