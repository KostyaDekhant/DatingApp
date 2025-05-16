package com.example.datingappclient.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileCardData {
    private final String name;
    private final int age;
    private final String description;
    private final List<UserImage> images;
}
