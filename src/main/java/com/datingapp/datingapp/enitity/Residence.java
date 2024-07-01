package com.datingapp.datingapp.enitity;

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
    private int pk_residence;
    @Column(name = "pk_user")
    private int pk_user;
    private String country;
    private String region;
    private String city;
    private int postalcode;

    public Residence(String country, String region, String city, int postalcode, int pk_user) {
        this.country = country;
        this.region = region;
        this.city = city;
        this.postalcode = postalcode;
        this.pk_user = pk_user;
    }

    public Residence() {
        this.pk_residence = -1;
        this.country = "";
        this.region = "";
        this.city = "";
        this.postalcode = 0;
        this.pk_user = 0;
    }

    @Override
    public String toString() {
        return "Residence{" +
                "pk_residence=" + pk_residence +
                ", pk_user='" + pk_user + '\'' +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                ", postalcode=" + postalcode +
                '}';
    }
}
