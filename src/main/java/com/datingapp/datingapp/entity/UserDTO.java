package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private LocalDate birthday;

    //@Positive
    @JsonProperty("height")
    private int height;

    //@NotBlank
    @JsonProperty("gender")
    private String gender;

    @JsonProperty("is_online")
    private Boolean is_online;

    @JsonProperty("last_online")
    private Timestamp last_online;

    @JsonProperty("description")
    private String description;


    public UserDTO(String name, LocalDate birthday, int height, String gender,
                   Boolean is_online, Timestamp last_online,
                   String description) {
        this.name = name;
        this.birthday = birthday;
        this.height = height;
        this.gender = gender;
        this.is_online = is_online;
        this.last_online = last_online;
        this.description = description;
    }

    public UserDTO() {
        this.id = -1;
        this.name = "";
        this.birthday = null;
        this.height = -1;
        this.gender = "";
        this.is_online = false;
        this.last_online = new Timestamp(1);
        this.description = "";
    }

    public UserDTO(User user){
        this.id = user.getPk_user();
        this.name = user.getName();
        this.birthday = user.getBirthday();
        this.height = user.getHeight();
        this.gender = user.getGender();
        this.is_online = user.getIs_online();
        this.last_online = user.getLast_online();
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
                ", \"is_online\"=" + is_online +
                ", \"last_online\"='" + last_online + '\'' +
                ", \"description\"='" + description + '\'' +
                '}';
    }
}
