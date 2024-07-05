package com.datingapp.datingapp.enitity;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Time;
import java.sql.Timestamp;

@Data
@Getter
@Setter
@Entity
@Table(name = "\"message\"")

public class Message {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_message")
    private int pk_message;

    @Column(name = "message")
    @JsonProperty("message")
    private String message;

    @Column(name = "time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp time;

    @Column(name = "pk_user")
    @JsonProperty("pk_user")
    private int pk_user;
    @Column(name = "pk_chat")
    @JsonProperty("pk_chat")
    private int pk_chat;

    public Message(String message, Timestamp time, int pk_user, int pk_chat) {
        this.message = message;
        this.time = time;
        this.pk_user = pk_user;
        this.pk_chat = pk_chat;
    }

    public Message() {
        this.pk_message = -1;
        this.message = "";
        this.time = new Timestamp(0);
        this.pk_user = -1;
        this.pk_chat = -1;
    }

    @Override
    public String toString() {
        return "Message{" +
                "pk_message=" + pk_message +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", pk_user=" + pk_user +
                ", pk_chat=" + pk_chat +
                '}';
    }
}
