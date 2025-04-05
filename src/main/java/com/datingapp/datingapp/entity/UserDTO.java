package com.datingapp.datingapp.entity;

import com.datingapp.datingapp.deserializers.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
//@Getter
//@Setter
@Entity
//@Table(name = "\"user\"")
public class UserDTO {
    @Id
    @JsonProperty("id")
    private int id;

    //@NotNull
    @JsonProperty("name")
    private String name;

    //@Positive
    @JsonProperty("birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    //@Positive
    @JsonProperty("height")
    private int height;

    //@NotBlank
    @JsonProperty("gender")
    private String gender;

    @JsonProperty("is_online")
    private Boolean isOnline;

    @JsonProperty("last_online")
    private Timestamp lastOnline;

    @JsonProperty("description")
    private String description;


    public UserDTO(String name, LocalDate birthday, int height, String gender,
                   Boolean isOnline, Timestamp lastOnline,
                   String description) {
        this.name = name;
        this.birthday = birthday;
        this.height = height;
        this.gender = gender;
        this.isOnline = isOnline;
        this.lastOnline = lastOnline;
        this.description = description;
    }

    public UserDTO() {
        this.id = -1;
        this.name = "";
        this.birthday = LocalDate.now();
        this.height = -1;
        this.gender = "";
        this.isOnline = false;
        this.lastOnline = new Timestamp(1);
        this.description = "";
    }

    public UserDTO(User user){
        this.id = user.getPkUser();
        this.name = user.getName();
        this.birthday = user.getBirthday();
        this.height = user.getHeight();
        this.gender = user.getGender();
        this.isOnline = user.getIsOnline();
        this.lastOnline = user.getLastOnline();
        this.description = user.getDescription();
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "\"id\"=" + id +
                ", \"name\"='" + name + '\'' +
                ", \"birthday\"=" + birthday +
                ", \"height\"=" + height +
                ", \"gender\"='" + gender + '\'' +
                ", \"is_online\"=" + isOnline +
                ", \"last_online\"='" + lastOnline + '\'' +
                ", \"description\"='" + description + '\'' +
                '}';
    }
}
