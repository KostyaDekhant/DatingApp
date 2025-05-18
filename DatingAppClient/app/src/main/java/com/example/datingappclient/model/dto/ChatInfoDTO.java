package com.example.datingappclient.model.dto;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ChatInfoDTO {

    @SerializedName("created_by")
    private Integer createdBy;

    @SerializedName("created_at")
    private Timestamp createdAt;

    @SerializedName("members")
    private List<ChatMemberDTO> members;

    @SerializedName("isGroup")
    private Boolean isGroup;


    public ChatMemberDTO findMemberById(int userId) {
        if (members == null) return null;
        for (ChatMemberDTO member : members) {
            if (member.getId() != null && member.getId() == userId) {
                return member;
            }
        }
        return null; // если не найден
    }
}