package com.example.datingappclient.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Message {
    @JsonProperty("pk_message")
    private int pk_message;

    @JsonProperty("message")
    private String message;

    @JsonProperty
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

    public Message() {

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

    public int getPk_user() {
        return pk_user;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPk_chat(int pk_chat) {
        this.pk_chat = pk_chat;
    }

    public void setPk_user(int pk_user) {
        this.pk_user = pk_user;
    }
}