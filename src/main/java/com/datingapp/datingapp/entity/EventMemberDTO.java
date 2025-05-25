package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EventMemberDTO {
    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("username")
    private String username;

    public EventMemberDTO(Integer userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public EventMemberDTO() {
    }
}
