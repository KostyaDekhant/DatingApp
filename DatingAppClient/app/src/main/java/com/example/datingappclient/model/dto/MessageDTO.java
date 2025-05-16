package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {
    @SerializedName("pk_message")
    private int id;

    @SerializedName("message")
    private String message;

    @SerializedName("time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp sendtime;

    @SerializedName("pk_user")
    private int senderId;

    @SerializedName("pk_chat")
    private int chatId;

    public MessageDTO(String message, Timestamp sendtime, int senderId, int chatId) {
        this.message = message;
        this.sendtime = sendtime;
        this.senderId = senderId;
        this.chatId = chatId;
    }
}