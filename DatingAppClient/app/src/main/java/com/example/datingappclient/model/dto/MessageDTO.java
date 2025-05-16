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
    private int pk_message;

    @SerializedName("message")
    private String message;

    @SerializedName("time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp time;

    @SerializedName("pk_user")
    private int pk_user;

    @SerializedName("pk_chat")
    private int pk_chat;

    public MessageDTO(String message, Timestamp time, int pk_user, int pk_chat) {
        this.message = message;
        this.time = time;
        this.pk_user = pk_user;
        this.pk_chat = pk_chat;
    }
}