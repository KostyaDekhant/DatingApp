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

    public GroupChatInfoDto(Integer createdBy, Timestamp createdAt, List<ChatMemberDTO> members) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.members = members;
    }

    public GroupChatInfoDto() {
    }
}
