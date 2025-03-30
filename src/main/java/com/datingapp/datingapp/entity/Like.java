package com.datingapp.datingapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@Entity
@Table(name = "\"like\"")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_like")
    private int pkLike;
    @Column(name = "liker")
    private int liker;
    @Column(name = "poster")
    private int poster;
    @Column(name = "time")
    private Timestamp time;

    public Like() {
        this.pkLike = -1;
        this.liker = -1;
        this.poster = -1;
        this.time = new Timestamp(0);
    }

    public Like(int liker, int poster, Timestamp time) {
        this.liker = liker;
        this.poster = poster;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Like{" +
                "pk_like=" + pkLike +
                ", liker=" + liker +
                ", poster=" + poster +
                ", time=" + time +
                '}';
    }
}
