package com.example.datingappclient.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class HistoryDTO {
    @JsonProperty("userId")
    private int userId;

    @JsonProperty("limit")
    private int limit;

    @JsonProperty("offset")
    private int offset;
}

