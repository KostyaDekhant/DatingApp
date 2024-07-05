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
@Table(name = "\"picture\"")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_picture")
    private int pk_picture;
    @Column(name = "url")
    private String url;
    @Column(name = "time")
    private Timestamp time;

    public Picture(String url, Timestamp time) {
        this.url = url;
        this.time = time;
    }

    public Picture() {
        this.pk_picture = -1;
        this.url = "";
        this.time = new Timestamp(0);
    }

    @Override
    public String toString() {
        return "Picture{" +
                "pk_picture=" + pk_picture +
                ", url='" + url + '\'' +
                ", time=" + time +
                '}';
    }
}
