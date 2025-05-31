package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

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
    @SerializedName("pk_event")
    private Integer id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("start_time")
    private Timestamp startTime;
    @SerializedName("end_time")
    private Timestamp endTime;
    @SerializedName("location")
    private String location;
    @SerializedName("capacity")
    private Integer capacity;
    @SerializedName("organizer_id")
    private Integer organizerId;
    @SerializedName("chat_id")
    private Integer chatId;

    @SerializedName("members")
    @JsonProperty("members")
    private List<EventMemberDTO> members;

    public boolean userIsMember(int userId) {
        for (EventMemberDTO member : members)
            if (member.getId() == userId) return true;
        return false;
    }

    public void leave(int userId) {
        if (members == null) return;
        members.removeIf(member -> member.getId() == userId);
    }

    public void join(int userId, String name) {
        if (!userIsMember(userId)) {
            members.add(new EventMemberDTO(userId, name));
        }
    }
}
