package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventsParamsDTO {
    @SerializedName("start_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime startTime;

    @SerializedName("end_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime endTime;

    @SerializedName("capacity")
    private Integer capacity;

    @SerializedName("limit")
    private Integer limit;

    @SerializedName("offset")
    private Integer offset;
}
