package com.datingapp.datingapp.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class UserPicId implements Serializable {
    private int pk_user;
    private int pk_picture;
}
