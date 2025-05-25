package com.datingapp.datingapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "event_participant")
public class EventParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_event_participant")
    private Integer pkEventParticipant;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "joined_at", nullable = false)
    private Timestamp joinedAt;
    @Column(name = "role", nullable = false)
    private String role;

    public EventParticipant(Event event, User user, Timestamp joinedAt, String role) {
        this.event = event;
        this.user = user;
        this.joinedAt = joinedAt;
        this.role = role;
    }

    public EventParticipant() {
    }
}