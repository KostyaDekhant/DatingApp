package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnlineStatusDTO {
    @JsonProperty("id")
    @SerializedName("id")
    private int userId;

    @JsonProperty("isOnline")
    private boolean isOnline;

    @JsonProperty("timestamp")
    private Timestamp timestamp;

    @Override
    public String toString() {
        return "user - " + userId + " online: " + isOnline;
    }
}
