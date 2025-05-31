package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnlineStatusDTO {
    @JsonProperty("id")
    private int userId;

    @JsonProperty("isOnline")
    private boolean isOnline;

    @Override
    public String toString() {
        return "user - " + userId + " online: " + isOnline;
    }
}
