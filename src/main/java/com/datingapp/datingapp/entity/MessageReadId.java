package com.datingapp.datingapp.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageReadId implements Serializable {

    private Long message;
    private Long user;
}
