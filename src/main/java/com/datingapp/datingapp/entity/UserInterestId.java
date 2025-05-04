package com.datingapp.datingapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserInterestId {
    @Column(name = "pk_user")
    private Integer userId;
    @Column(name = "pk_interest")
    private Integer interestId;

    public UserInterestId(Integer userId, Integer interestId) {
        this.userId = userId;
        this.interestId = interestId;
    }

    public UserInterestId() {
    }

    public Integer getUserId() {return  userId;}

    public Integer getInterestId() { return  interestId;}
}
