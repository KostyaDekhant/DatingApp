package com.datingapp.datingapp.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
//@Getter
//@Setter
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_user")
    @JsonProperty("pk_user")
    private int pk_user;

    //@NotBlank(message = "Имя не должно быть пустым")
    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    //@Positive(message = "Возраст должен быть положительным")
    @JsonProperty("birthday")
    private LocalDate birthday;

    //@Positive(message = "Рост должен быть положительным")
    @JsonProperty("height")
    private int height;

    //@NotBlank(message = "Пол не должен быть пустым")
    @JsonProperty("gender")
    private String gender;

    @JsonProperty("is_online")
    private Boolean is_online;

    @JsonProperty("last_online")
    private Timestamp last_online;

    //@NotBlank(message = "Пароль не должен быть пустым")
    @JsonProperty("password")
    private String password;

    @JsonProperty("description")
    private String description;

    //@NotBlank(message = "Логин не должен быть пустым")
    @Column(unique = true)
    @JsonProperty("login")
    private String login;

    @JsonProperty("salt")
    private String salt;

    public User(String name, LocalDate birthday, int height, String gender,
                Boolean is_online, Timestamp last_online, String password,
                String description, String login, String salt) {
        this.name = name;
        this.birthday = birthday;
        this.height = height;
        this.gender = gender;
        this.is_online = is_online;
        this.last_online = last_online;
        this.password = password;
        this.description = description;
        this.login = login;
        this.salt = salt;
    }

    public User() {
        this.pk_user = -1;
        this.name = "";
        this.birthday = null;
        this.height = -1;
        this.gender = "";
        this.is_online = false;
        this.last_online = new Timestamp(1);
        this.password = "";
        this.description = "";
        this.login = "";
        this.salt = null;
    }

    @Override
    public String toString() {
        return "User{" +
                "\"pk_user\"=" + pk_user +
                ", \"name\"='" + name + '\'' +
                ", \"birthday\"=" + birthday +
                ", \"height\"=" + height +
                ", \"gender\"='" + gender + '\'' +
                ", \"is_online\"=" + is_online +
                ", \"last_online\"='" + last_online + '\'' +
                ", \"password\"='" + password + '\'' +
                ", \"description\"='" + description + '\'' +
                ", \"login\"='" + login + '\'' +
                ", \"salt\"='" + salt + '\'' +
                '}';
    }
}
