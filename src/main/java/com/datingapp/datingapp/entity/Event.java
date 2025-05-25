package com.datingapp.datingapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_event")
    private Integer pkEvent;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "start_time")
    private Timestamp startTime;
    @Column(name = "end_time")
    private Timestamp endTime;
    @Column(name = "location")
    private String location;
    @Column(name = "capacity", nullable = false)
    private Integer capacity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private GroupChat chat;


    public Event(int pkEvent, String title, String description, Timestamp starTime,
                 Timestamp endTime, String location, int capacity, User organizerId) {
        this.pkEvent = pkEvent;
        this.title = title;
        this.description = description;
        this.startTime = starTime;
        this.endTime = endTime;
        this.location = location;
        this.capacity = capacity;
        this.organizerId = organizerId;
    }

    public Event(EventDTO eventDTO) {
        this.title = eventDTO.getTitle();
        this.description = eventDTO.getDescription();
        this.startTime = eventDTO.getStartTime();
        this.endTime = eventDTO.getEndTime();
        this.location = eventDTO.getLocation();
        this.capacity = eventDTO.getCapacity();
    }

    public Event() {
    }
}
