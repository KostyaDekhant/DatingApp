package com.example.datingappclient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Message {
    @JsonProperty("message")
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private String time;

    @JsonProperty("pk_user")
    private int pk_user;

    @JsonProperty("pk_chat")
    private int pk_chat;

    public Message(String message, String time, int pk_user, int pk_chat) {
        this.message = message;
        this.time = time;
        this.pk_user = pk_user;
        this.pk_chat = pk_chat;
    }


    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", pk_user=" + pk_user +
                ", pk_chat=" + pk_chat +
                '}';
    }
}