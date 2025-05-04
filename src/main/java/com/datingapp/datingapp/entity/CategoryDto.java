package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryDto {
    @JsonProperty("pk_category")
    private Integer pkCategory;

    @JsonProperty("name")
    private String name;

    public CategoryDto(Integer pkCategory, String name) {
        this.pkCategory = pkCategory;
        this.name = name;
    }

    public CategoryDto() {
    }
}
