package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Objects;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {
    @JsonProperty("pk_message")
    @SerializedName("pk_message")
    private int id;

    @JsonProperty("message")
    private String message;

    @JsonProperty("time")
    @SerializedName("time")
    private Timestamp sendtime;

    @JsonProperty("pk_user")
    private int senderId;

    @JsonProperty("pk_chat")
    @SerializedName("pk_chat")
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

    public MessageDTO(MessageDTO other) {
        this.id = other.id;
        this.message = other.message;
        this.sendtime = other.sendtime != null ? new Timestamp(other.sendtime.getTime()) : null;
        this.senderId = other.senderId;
        this.chatId = other.chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageDTO messageDTO)) return false;

        return id == messageDTO.id &&
                senderId == messageDTO.senderId &&
                chatId == messageDTO.chatId &&
                Objects.equals(message, messageDTO.message) &&
                Objects.equals(sendtime, messageDTO.sendtime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, sendtime, senderId, chatId);
    }
}