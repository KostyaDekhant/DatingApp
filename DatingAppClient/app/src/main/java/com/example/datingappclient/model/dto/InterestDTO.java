package com.example.datingappclient.model.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterestDTO{

    @SerializedName("pk_interest")
    private Integer id;

    @SerializedName("pk_category")
    private Integer categoryId;

    private String name;

    @Override
    public String toString() {
        return name;
    }

}