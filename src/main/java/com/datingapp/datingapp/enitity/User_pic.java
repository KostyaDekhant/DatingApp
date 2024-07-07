package com.datingapp.datingapp.enitity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Entity
@IdClass(UserPicId.class)
@Table(name = "\"user_pic\"")
public class User_pic {
    @Id
    @Column(name = "pk_user")
    private int pk_user;
    @Id
    @Column(name = "pk_picture")
    private int pk_picture;

    public User_pic(int pk_picture, int pk_user) {
        this.pk_picture = pk_picture;
        this.pk_user = pk_user;
    }

    public User_pic() {
        this.pk_picture = -1;
        this.pk_user = -1;
    }

    @Override
    public String toString() {
        return "User_pic{" +
                "pk_user=" + pk_user +
                ", pk_picture=" + pk_picture +
                '}';
    }
}



