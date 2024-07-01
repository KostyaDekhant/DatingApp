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
    @Column(name = "pk_user")
    private int pk_user;
    @Column(name = "name")
    private String name;
    private int age;
    private int height;
    private Boolean is_online;
    private Timestamp last_online;
    private String gender;
    private String password;
    private String description;
    @Column(unique = true)
    private String login;

    public User(String name, int age, int height, String gender,
                Boolean is_online, Timestamp last_online, String password,
                String description, String login) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.is_online = is_online;
        this.last_online = last_online;
        this.password = password;
        this.description = description;
        this.login = login;
    }

    public User() {
        this.pk_user = -1;
        this.name = "";
        this.age = -1;
        this.height = -1;
        this.gender = "";
        this.is_online = false;
        this.last_online = new Timestamp(1);
        this.password = "";
        this.description = "";
        this.login = "";
    }

    @Override
    public String toString() {
        return "User{" +
                "\"pk_user\"=" + pk_user +
                ", \"name\"='" + name + '\'' +
                ", \"age\"=" + age +
                ", \"height\"=" + height +
                ", \"gender\"='" + gender + '\'' +
                ", \"is_online\"=" + is_online +
                ", \"last_online\"='" + last_online + '\'' +
                ", \"password\"='" + password + '\'' +
                ", \"description\"='" + description + '\'' +
                ", \"login\"='" + login + '\'' +
                '}';
    }
}
