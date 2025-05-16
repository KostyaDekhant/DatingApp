package com.example.datingappclient.model.dto;

import androidx.annotation.NonNull;

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

    @NonNull
    @Override
    public String toString() {
        return id + ":" + name ;
    }
}