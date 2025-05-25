package com.example.datingappclient.model;

import com.example.datingappclient.model.dto.UserInterestDTO;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class ProfileCardData {
    private int userId;
    private String name;
    private int age;
    private String description;
    private List<UserImage> images;
    private List<UserInterestDTO> interests;
}
