package com.datingapp.datingapp.entity;

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
    private int pkChat;
    @Column(name = "pk_user")
    @JsonProperty("pk_user")
    private int pkUser;
    @Column(name = "pk_user1")
    @JsonProperty("pk_user1")
    private int pkUser1;

    public Chat() {
        this.pkChat = -1;
        this.pkUser = -1;
        this.pkUser1 = -1;
    }

    public Chat(int pkUser, int pkUser1) {
        this.pkUser = pkUser;
        this.pkUser1 = pkUser1;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "pk_chat=" + pkChat +
                ", pk_user=" + pkUser +
                ", pk_user1=" + pkUser1 +
                '}';
    }
}
