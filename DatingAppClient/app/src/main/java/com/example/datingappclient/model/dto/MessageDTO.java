package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class MessageDTO {
    @JsonProperty("pk_message")
    private int id;

    @JsonProperty("message")
    private String message;

    @JsonProperty("time")
    @SerializedName("time")
    private Timestamp sendtime;

    @JsonProperty("pk_user")
    private int senderId;

    @JsonProperty("pk_chat")
    private int chatId;

    public MessageDTO(String message, Timestamp sendtime, int senderId, int chatId) {
        this.message = message;
        this.sendtime = sendtime;
        this.senderId = senderId;
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", sendtime=" + sendtime +
                ", senderId=" + senderId +
                ", chatId=" + chatId +
                '}';
    }
}