package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LikeDTO {
    @JsonProperty("liker")
    private int liker;
    @JsonProperty("poster")
    private int poster;
    @JsonProperty("time")
    private Timestamp time;
    @JsonProperty("name")
    private String name;
    @JsonProperty("image")
    private byte[] image;
    @JsonProperty("birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    public LikeDTO(int liker, int poster, Timestamp time, String name,
                   byte[] image, LocalDate birthday) {
        this.liker = liker;
        this.poster = poster;
        this.time = time;
        this.name = name;
        this.image = image;
        this.birthday = birthday;
    }
}