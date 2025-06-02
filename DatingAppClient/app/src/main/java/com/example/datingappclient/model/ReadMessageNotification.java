package com.example.datingappclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReadMessageNotification {
    @JsonProperty("userId")
    int userId;

    @JsonProperty("messageIds")
    List<Integer> messageIds;
}
