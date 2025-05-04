package com.datingapp.datingapp.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class UserProfileFieldId {
    private Integer userId;
    private Integer fieldId;

    public UserProfileFieldId(int userId, Integer id) {
        this.userId = userId;
        this.fieldId = id;
    }

    public UserProfileFieldId() {

    }

    public Integer getFieldId() {
        return fieldId;
    }
}
