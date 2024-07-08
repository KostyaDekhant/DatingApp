package com.datingapp.datingapp.enitity;

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
    private int pk_like;
    @Column(name = "liker")
    private int liker;
    @Column(name = "poster")
    private int poster;
    @Column(name = "time")
    private Timestamp time;
    //@Column(name = "pk_picture")
    //private int pk_picture;

    public Like() {
        this.pk_like = -1;
        this.liker = -1;
        this.poster = -1;
        this.time = new Timestamp(0);
        //this.pk_picture = -1;
    }

    public Like(int liker, int poster, Timestamp time) { //, int pk_picture
        this.liker = liker;
        this.poster = poster;
        this.time = time;
        //this.pk_picture = pk_picture;
    }

    @Override
    public String toString() {
        return "Like{" +
                "pk_like=" + pk_like +
                ", liker=" + liker +
                ", poster=" + poster +
                ", time=" + time +
                //", pk_picture=" + pk_picture +
                '}';
    }
}
