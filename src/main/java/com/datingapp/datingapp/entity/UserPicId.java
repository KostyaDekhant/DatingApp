package com.datingapp.datingapp.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class UserPicId implements Serializable {
    private int pkUser;
    private int pkPicture;
}
