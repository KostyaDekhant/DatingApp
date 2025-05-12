package com.example.datingappclient.model.dto;

import com.google.gson.annotations.SerializedName;

public class ChatMemberDTO {
    @SerializedName("username")
    private String username;
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("avatar")
    private byte[]  avatar;
}
