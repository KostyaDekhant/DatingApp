package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventDTO {
    @JsonProperty("pk_event")
    private Integer id;
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
    private Integer chatId;
    @JsonProperty("members")
    private List<EventMemberDTO> members;
}
