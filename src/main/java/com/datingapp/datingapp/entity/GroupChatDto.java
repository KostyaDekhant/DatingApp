package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Data
public class GroupChatDto{
    @JsonProperty("pk_group_chat")
    private Integer pkGroupChat;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private byte[] image;

    @JsonProperty("last_message")
    private String lastMessage;

    @JsonProperty("group_chat_info")
    private GroupChatInfoDto groupChatInfoDto;


    public GroupChatDto() {
        this.pkGroupChat = -1;
        this.name = "";
        this.lastMessage = "";
        //this.createdBy = -1;
        //this.createdAt = null;
        this.image = null;
        //this.members = null;
    }

    public GroupChatDto(Integer pkGroupChat, String name
            //, int createdBy, Timestamp createdAt
            , byte[] image, String lastMessage) {
                        //, List<ChatMemberDTO> members) {
        this.pkGroupChat = pkGroupChat;
        this.name = name;
        this.lastMessage = lastMessage;
//        this.createdBy = createdBy;
//        this.createdAt = createdAt;
        this.image = image;
        //this.members = members;
    }

}
