package com.example.datingappclient.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ChatInfoDTO {
    @JsonProperty("created_by")
    @SerializedName("created_by")
    private Integer createdBy;

    @JsonProperty("created_at")
    @SerializedName("created_at")
    private Timestamp createdAt;

    @JsonProperty("members")
    @SerializedName("members")
    private List<ChatMemberDTO> members;

    @JsonProperty("isGroup")
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