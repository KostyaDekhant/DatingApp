package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class EventDTO {
    @JsonProperty("pk_event")
    private Integer pkEvent;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("start_time")
    private Timestamp startTime;
    @JsonProperty("end_time")
    private Timestamp endTime;
    @JsonProperty("location")
    private String location;
    @JsonProperty("capacity")
    private Integer capacity;
    @JsonProperty("organizer_id")
    private Integer organizerId;
    @JsonProperty("chat_id")
    private Integer chat;
    @JsonProperty("members")
    private List<EventMemberDTO> members;

    public EventDTO(Event event, List<EventMemberDTO> members) {
        this.pkEvent = event.getPkEvent();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.location = event.getLocation();
        this.capacity = event.getCapacity();
        this.organizerId = event.getOrganizerId().getPkUser();
        this.chat = event.getChat() == null ? -1 : event.getChat().getPkGroupChat();
        this.members = members;
    }

    public EventDTO() {
    }
}
