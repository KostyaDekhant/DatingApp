package com.example.datingappclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReadMessagePayload {
    @JsonProperty("userId")
    int userId;

    @JsonProperty("chatId")
    int chatId;

    @JsonProperty("readMessageIds")
    List<Integer> readMessageIds;
}
