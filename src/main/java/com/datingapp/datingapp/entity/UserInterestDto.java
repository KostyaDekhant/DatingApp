package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserInterestDto {
    @JsonProperty("pk_interest")
    private Integer pkInterest;

    @JsonProperty("name")
    private String name;

    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("description")
    private String description;

    public UserInterestDto(Integer pkInterest, String name, Integer weight, String description) {
        this.pkInterest = pkInterest;
        this.name = name;
        this.weight = weight;
        this.description = description;
    }

    public UserInterestDto() {
    }
}
