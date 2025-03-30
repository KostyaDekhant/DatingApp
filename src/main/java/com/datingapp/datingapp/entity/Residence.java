package com.datingapp.datingapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@Entity
@Table(name = "\"residence\"")
public class Residence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pkResidence;
    @Column(name = "pk_user")
    private int pkUser;
    private String country;
    private String region;
    private String city;
    private int postalcode;

    public Residence(String country, String region, String city, int postalcode, int pkUser) {
        this.country = country;
        this.region = region;
        this.city = city;
        this.postalcode = postalcode;
        this.pkUser = pkUser;
    }

    public Residence() {
        this.pkResidence = -1;
        this.country = "";
        this.region = "";
        this.city = "";
        this.postalcode = 0;
        this.pkUser = 0;
    }

    @Override
    public String toString() {
        return "Residence{" +
                "pk_residence=" + pkResidence +
                ", pk_user='" + pkUser + '\'' +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                ", postalcode=" + postalcode +
                '}';
    }
}
