package com.example.datingappclient.model.dto;

import com.google.gson.annotations.SerializedName;

public class GroupChatDTO {
    @SerializedName("pk_group_chat")
    private Integer pkGroupChat;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private byte[] image;

    @SerializedName("group_chat_info")
    private GroupChatInfoDTO groupChatInfo;
}
