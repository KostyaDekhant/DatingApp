package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class FormDTO {
    @SerializedName("name")
    private String name;

    @SerializedName("birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @SerializedName("gender")
    private String gender;

    @SerializedName("height")
    private Integer height;

    @SerializedName("description")
    private String description;

    @SerializedName("pk_user")
    private Integer userId;
}
