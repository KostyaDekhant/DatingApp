package com.example.datingappclient.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthDTO {
    @SerializedName("login")
    private String login;

    @SerializedName("password")
    private String password;
}
