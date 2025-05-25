package com.datingapp.datingapp.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupChatDto{
    @JsonProperty("pk_group_chat")
    private Integer pkGroupChat;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private byte[] image;

    @JsonProperty("last_message")
    private MessageDTO lastMessage;

    @JsonProperty("group_chat_info")
    private GroupChatInfoDto groupChatInfoDto;


    public GroupChatDto() {
        this.name = "";
        this.lastMessage = null;
        //this.createdBy = -1;
        //this.createdAt = null;
        this.image = null;
        this.groupChatInfoDto = null;
        //this.members = null;
    }

    public GroupChatDto(Integer pkGroupChat, String name
            //, int createdBy, Timestamp createdAt
            , byte[] image, MessageDTO lastMessage) {
                        //, List<ChatMemberDTO> members) {
        this.pkGroupChat = pkGroupChat;
        this.name = name;
        this.lastMessage = lastMessage;
//        this.createdBy = createdBy;
//        this.createdAt = createdAt;
        this.image = image;
        //this.members = members;
    }


    @Override
    public String toString() {
        return "GroupChatDto{" +
                "pkGroupChat=" + pkGroupChat +
                ", name='" + name + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", groupChatInfoDto=" + groupChatInfoDto +
                '}';
    }
}
