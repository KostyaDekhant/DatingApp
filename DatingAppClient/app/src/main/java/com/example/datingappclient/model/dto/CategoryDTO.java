package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    @SerializedName("pk_category")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @Override
    public String toString() {
        return id + ":" + name ;
    }
}