package com.datingapp.datingapp.entity;

import lombok.Data;

@Data
public class UserFieldDto {
    private String fieldKey;
    private String label;
    private String description;
    private String dataType;
    private Object value;
}