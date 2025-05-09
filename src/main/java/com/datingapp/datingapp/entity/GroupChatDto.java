package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class GroupChatDto{
    @JsonProperty("pk_group_chat")
    private Integer pkGroupChat;

    @JsonProperty("name")
    private String name;

//    @JsonProperty("created_by")
//    private Integer createdBy;
//
//    @JsonProperty("created_at")
//    private Timestamp createdAt;

    @JsonProperty("image")
    private byte[] image;

//    @JsonProperty("members")
//    private List<ChatMemberDTO> members;

    public GroupChatDto() {
        this.pkGroupChat = -1;
        this.name = "";
        //this.createdBy = -1;
        //this.createdAt = null;
        this.image = null;
        //this.members = null;
    }

    public GroupChatDto(Integer pkGroupChat, String name
            //, int createdBy, Timestamp createdAt
            , byte[] image) {
                        //, List<ChatMemberDTO> members) {
        this.pkGroupChat = pkGroupChat;
        this.name = name;
//        this.createdBy = createdBy;
//        this.createdAt = createdAt;
        this.image = image;
        //this.members = members;
    }
}
