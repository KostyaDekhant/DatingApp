package com.datingapp.datingapp.enitity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Data
@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_user")
    @JsonProperty("pk_user")
    private int pk_user;
    @Column(name = "name")
    @JsonProperty("name")
    private String name;
    @JsonProperty("age")
    private LocalDate age;
    @JsonProperty("height")
    private int height;
    @JsonProperty("is_online")
    private Boolean is_online;
    @JsonProperty("last_online")
    private Timestamp last_online;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("password")
    private String password;
    @JsonProperty("description")
    private String description;
    @Column(unique = true)
    @JsonProperty("login")
    private String login;

    public User(String name, LocalDate age, int height, String gender,
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
        this.age = null;
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
