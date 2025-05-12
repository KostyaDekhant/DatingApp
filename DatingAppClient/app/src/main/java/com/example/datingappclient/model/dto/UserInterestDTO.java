package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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