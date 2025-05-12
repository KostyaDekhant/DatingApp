package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "chat_member")
public class ChatMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    @JsonProperty("chat_id")
    private Integer chatId;

    @Column(name = "user_id")
    @JsonProperty("user_id")
    private Integer userId;

    @Column(name = "joined_at")
    @JsonProperty("joined_at")
    private Timestamp joinedAt;

    @Column(name = "role")
    @JsonProperty("role")
    private String role;

    public ChatMember(int chatId, int userId, Timestamp joinedAt, String role) {
        this.chatId = chatId;
        this.userId = userId;
        this.joinedAt = joinedAt;
        this.role = role;
    }

    public ChatMember() {
        this.chatId = -1;
        this.userId = -1;
        this.joinedAt = null;
        this.role = "";
    }
}

