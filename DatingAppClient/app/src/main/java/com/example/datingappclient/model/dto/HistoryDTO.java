package com.example.datingappclient.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class HistoryDTO {
    @JsonProperty("limit")
    private int limit;

    @JsonProperty("offset")
    private int offset;
}

