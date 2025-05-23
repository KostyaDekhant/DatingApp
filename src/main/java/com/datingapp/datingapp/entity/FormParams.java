package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FormParams {
    @JsonProperty("userId")
    private int userId;
    @JsonProperty("age_min")
    private int age_min;
    @JsonProperty("age_max")
    private int age_max;
    @JsonProperty("height_min")
    private int height_min;
    @JsonProperty("height_max")
    private int height_max;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("offset")
    private int offset;
}
