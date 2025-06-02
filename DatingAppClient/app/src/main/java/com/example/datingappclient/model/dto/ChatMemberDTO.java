package com.example.datingappclient.model.dto;

import com.example.datingappclient.utils.gson.ByteDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberDTO {
    @JsonProperty("username")
    @SerializedName("username")
    private String name;

    @JsonProperty("userId")
    @SerializedName("userId")
    private Integer id;

    @JsonProperty("avatar")
    @SerializedName("avatar")
    @JsonAdapter(ByteDeserializer.class)
    private byte[] image;

    @Setter
    @JsonIgnore
    private transient boolean isAlreadyInChat;
    @Setter
    @JsonIgnore
    private transient boolean chatOwner;

    public ChatMemberDTO(ChatMemberDTO other) {
        this.name = other.name;
        this.id = other.id;
        this.image = other.image != null ? Arrays.copyOf(other.image, other.image.length) : null;
        this.isAlreadyInChat = other.isAlreadyInChat;
        this.chatOwner = other.chatOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMemberDTO chatMember)) return false;
        return Objects.equals(id, chatMember.id) &&
                Objects.equals(name, chatMember.name) &&
                Arrays.equals(image, chatMember.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, id);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
