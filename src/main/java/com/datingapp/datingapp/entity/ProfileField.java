package com.datingapp.datingapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "profile_field")
public class ProfileField {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fieldKey;
    private String label;
    private String description;
    private String dataType;
    private Integer sortOrder;
}
