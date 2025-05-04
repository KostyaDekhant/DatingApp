package com.datingapp.datingapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "user_interest")
public class UserInterest {
    @EmbeddedId
    private UserInterestId id;
//    @Id
//    @Column(name = "pk_user")
//    private Integer pkUser;
//    @Id
//    @Column(name = "pk_interest")
//    private Integer pkInterest;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "description")
    private String description;

}
