package com.datingapp.datingapp.entity;

import jakarta.persistence.Column;
import lombok.*;

import java.sql.Timestamp;

@Data
@Getter
@Setter
public class ChatMessageDto {
    @Column(name = "message")
    private String message;

    @Column(name = "time")
    private Timestamp time;

    @Column(name = "pk_user")
    private int pkUser;

    @Column(name = "pk_user1")
    private int pkUser2;

    public ChatMessageDto(String message, Timestamp time, int pkUser, int pkUser2) {
        this.message = message;
        this.time = time;
        this.pkUser = pkUser;
        this.pkUser2 = pkUser2;
    }

    public ChatMessageDto() {
        this.message = "";
        this.time = new Timestamp(0);
        this.pkUser = -1;
        this.pkUser2 = -1;
    }
}
