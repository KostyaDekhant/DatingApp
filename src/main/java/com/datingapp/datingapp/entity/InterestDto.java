package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InterestDto {

    @JsonProperty("pk_interest")
    private Integer pkInterest;

    @JsonProperty("pk_category")
    private Integer pkCategory;

    @JsonProperty("name")
    private String name;

    public InterestDto() { }

    public InterestDto(Integer pkInterest, Integer pkCategory, String name) {
        this.pkInterest = pkInterest;
        this.pkCategory = pkCategory;
        this.name       = name;
    }

}
