package com.datingapp.datingapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "user_profile_field")
public class UserProfileField {
    @EmbeddedId
    private UserProfileFieldId id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "description")
    private Object description;
}
