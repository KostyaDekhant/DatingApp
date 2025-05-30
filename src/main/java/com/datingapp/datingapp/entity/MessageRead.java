package com.datingapp.datingapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "message_read")
@IdClass(MessageReadId.class)
public class MessageRead {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "read_at", nullable = false)
    private Timestamp readAt;

    public MessageRead(Message message, User user) {
        this.message = message;
        this.user = user;
    }

    public MessageRead() {

    }
}
