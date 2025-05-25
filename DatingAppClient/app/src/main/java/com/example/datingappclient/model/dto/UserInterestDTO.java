package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInterestDTO {
    @SerializedName("pk_interest")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("description")
    private String description;
}