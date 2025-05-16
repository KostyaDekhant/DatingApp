package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MessageDTO {
    @JsonProperty("pk_message")
    private int pk_message;

    @JsonProperty("message")
    private String message;

    @JsonProperty("time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp time;

    @JsonProperty("pk_user")
    private int pkUser;

    @JsonProperty("pk_chat")
    private int pkChat;

    public MessageDTO(Message message) {
        this.pk_message = message.getPkMessage();
        this.message = message.getMessage();
        this.time = message.getTime();
        this.pkUser = message.getPkUser();
        this.pkChat = message.getPkChat();
    }
}