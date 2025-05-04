package com.example.datingappclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterestDTO{

    @JsonProperty("pk_interest")
    private Integer pkInterest;

    @JsonProperty("pk_category")
    private Integer pkCategory;

    private String name;

    @Override
    public String toString() {
        return name;
    }

}