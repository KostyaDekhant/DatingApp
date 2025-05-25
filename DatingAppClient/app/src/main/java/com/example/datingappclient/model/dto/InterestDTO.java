package com.example.datingappclient.model.dto;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class InterestDTO{

    @SerializedName("pk_interest")
    private Integer id;

    @SerializedName("pk_category")
    private Integer categoryId;

    private String name;

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}