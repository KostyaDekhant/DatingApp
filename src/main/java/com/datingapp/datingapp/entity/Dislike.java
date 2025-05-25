package com.datingapp.datingapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "\"dislike\"")
public class Dislike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_dislike")
    private int pkDislike;
    @Column(name = "disliker")
    private int liker;
    @Column(name = "poster")
    private int poster;
    @Column(name = "time")
    private Timestamp time;

    public Dislike() {
    }

    public Dislike(int liker, int poster, Timestamp time) {
        this.liker = liker;
        this.poster = poster;
        this.time = time;
    }
}
