package com.datingapp.datingapp.enitity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "\"chat\"")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk_chat;
    @Column(name = "pk_user")
    @JsonProperty("pk_user")
    private int pk_user;
    @Column(name = "pk_user1")
    @JsonProperty("pk_user1")
    private int pk_user1;

    public Chat() {
        this.pk_chat = -1;
        this.pk_user = -1;
        this.pk_user1 = -1;
    }

    public Chat(int pk_user, int pk_user1) {
        this.pk_user = pk_user;
        this.pk_user1 = pk_user1;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "pk_chat=" + pk_chat +
                ", pk_user=" + pk_user +
                ", pk_user1=" + pk_user1 +
                '}';
    }
}
