package com.example.datingappclient.model.dto;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupChatInfoDTO {
    @SerializedName("created_by")
    private Integer createdBy;

    @SerializedName("created_at")
    private Timestamp createdAt;

    @SerializedName("members")
    private List<ChatMemberDTO> members;

    @SerializedName("isGroup")
    private Boolean isGroup;
}
