package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "group_chat")
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_group_chat")
    @JsonProperty("pk_group_chat")
    private Integer pkGroupChat;

    //@NotBlank(message = "Имя не должно быть пустым")
    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "is_group")
    @JsonProperty("is_group")
    private Boolean isGroup;

    @Column(name = "created_by")
    @JsonProperty("created_by")
    private Integer createdBy;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private Timestamp createdAt;

    @Column(name = "image")
    @JsonProperty("image")
    private byte[] image;

    public GroupChat() {
        this.name = "";
        this.isGroup = false;
        this.createdBy = -1;
        this.createdAt = null;
        this.image = null;
    }

    public GroupChat(int pkGroupChat, String name, Boolean isGroup, int createdBy,
                     Timestamp createdAt, byte[] image) {
        this.pkGroupChat = pkGroupChat;
        this.name = name;
        this.isGroup = isGroup;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.image = image;
    }
}
