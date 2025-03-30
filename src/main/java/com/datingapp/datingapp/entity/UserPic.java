package com.datingapp.datingapp.entity;


import jakarta.persistence.*;


@Entity
@IdClass(UserPicId.class)
@Table(name = "\"user_pic\"")
public class UserPic {
    @Id
    @Column(name = "pk_user")
    private int pkUser;
    @Id
    @Column(name = "pk_picture")
    private int pkPicture;

    public UserPic(int pkPicture, int pkUser) {
        this.pkPicture = pkPicture;
        this.pkUser = pkUser;
    }

    public UserPic() {
        this.pkPicture = -1;
        this.pkUser = -1;
    }

    @Override
    public String toString() {
        return "UserPic{" +
                "pk_user=" + pkUser +
                ", pk_picture=" + pkPicture +
                '}';
    }
}



