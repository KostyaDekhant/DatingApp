package com.datingapp.datingapp.enitity;

import jakarta.persistence.Entity;
import lombok.*;

import java.sql.Timestamp;

@Data
@Getter
@Setter
public class ChatMessageDto {
    private String message;
    private Timestamp time;
    private int pkUser;
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
