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
    private Long id;

    // связь с группой
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    @JsonProperty("chat_id")
    private GroupChat chatId;

    @Column(name = "user_id")
    @JsonProperty("user_id")
    private Integer userId;

    @Column(name = "joined_at")
    @JsonProperty("joined_at")
    private Timestamp joinedAt;

    @Column(name = "role")
    @JsonProperty("role")
    private String role;

    public ChatMember(GroupChat chatId, int userId, Timestamp joinedAt, String role) {
        this.chatId = chatId;
        this.userId = userId;
        this.joinedAt = joinedAt;
        this.role = role;
    }

    public ChatMember() {
        this.userId = -1;
        this.joinedAt = null;
        this.role = "";
    }
}

