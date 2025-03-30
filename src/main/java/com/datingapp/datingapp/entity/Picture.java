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
@Table(name = "\"picture\"")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_picture")
    private int pkPicture;
    @Column(name = "id")
    private int id;
    @Column(name = "time")
    private Timestamp time;
    @Column(name = "image")
    private byte[] bytea;


    public Picture(int id, Timestamp time, byte[] bytea) {
        this.id = id;
        this.time = time;
        this.bytea = bytea;
    }

    public Picture() {
        this.pkPicture = -1;
        this.id = -1;
        this.time = new Timestamp(0);
        this.bytea = null;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "pk_picture=" + pkPicture +
                ", time=" + time +
                ", id='" + id + '\'' +
                ", image='" + bytea + '\'' +
                '}';
    }
}
