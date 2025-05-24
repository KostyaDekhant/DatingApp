package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FormsParametersDTO {
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
