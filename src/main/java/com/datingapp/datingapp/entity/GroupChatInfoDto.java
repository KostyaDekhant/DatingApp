package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class GroupChatInfoDto {
    @JsonProperty("created_by")
    private Integer createdBy;

    @JsonProperty("created_at")
    private Timestamp createdAt;

    @JsonProperty("members")
    private List<ChatMemberDTO> members;

    @JsonProperty("isGroup")
    private Boolean isGroup;

    public GroupChatInfoDto(Integer createdBy, Timestamp createdAt, List<ChatMemberDTO> members, Boolean isGroup) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.members = members;
        this.isGroup = isGroup;
    }

    public GroupChatInfoDto() {
    }

    @Override
    public String toString() {
        return "GroupChatInfoDto{" +
                "createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", members=" + members +
                ", isGroup=" + isGroup +
                '}';
    }
}
