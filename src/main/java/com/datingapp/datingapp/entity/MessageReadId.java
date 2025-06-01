package com.datingapp.datingapp.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageReadId implements Serializable {

    private Integer message;
    private Integer user;
}
