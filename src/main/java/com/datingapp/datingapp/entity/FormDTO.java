package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FormDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("description")
    private String description;

    @JsonProperty("pk_user")
    private Integer pk_user;

    public FormDTO(String name, LocalDate birthday, String gender, int height, String description, Integer pk_user) {
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.height = height;
        this.description = description;
        this.pk_user = pk_user;
    }

    public FormDTO() {

    }
}
