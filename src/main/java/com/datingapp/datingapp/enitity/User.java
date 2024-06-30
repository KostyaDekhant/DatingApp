package com.datingapp.datingapp.enitity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk_user;

    @Column(unique = true)
    private String name;
    private int age;
    private int height;
    private int pk_residence;
    private Boolean is_online;
    private Timestamp last_online;
    private String gender;
    private String password;

    public User(String name, int age, int height, String gender, int pk_residence,
                Boolean is_online, Timestamp last_online, String password) {
        //this.id = id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.pk_residence = pk_residence;
        this.is_online = is_online;
        this.last_online = last_online;
        this.password = password;
    }

    public User() {
        this.pk_user = 20;
        this.name = "lol";
        this.age = 1;
        this.height = 1;
        this.gender = "p";
        this.pk_residence = 1;
        this.is_online = true;
        this.last_online = new Timestamp(1);
        this.password = "password";
    }

    @Override
    public String toString() {
        return "User{" +
                "\"pk_user\"=" + pk_user +
                ", \"name\"='" + name + '\'' +
                ", \"age\"=" + age +
                ", \"height\"=" + height +
                ", \"gender\"='" + gender + '\'' +
                ", \"pk_residence\"='" + pk_residence + '\'' +
                ", \"is_online\"=" + is_online +
                ", \"last_online\"='" + last_online + '\'' +
                ", \"password\"='" + password + '\'' +
                '}';
    }
}
