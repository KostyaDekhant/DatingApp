package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReadMessagePayload {
    @JsonProperty("userId")
    int userId;
    @JsonProperty("chatId")
    int chatId;
    @JsonProperty("readMessageIds")
    List<Integer> readMessageIds;
}
