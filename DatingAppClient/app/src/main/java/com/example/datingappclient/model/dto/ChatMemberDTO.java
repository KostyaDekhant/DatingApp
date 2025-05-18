package com.example.datingappclient.model.dto;

import com.example.datingappclient.utils.gson.ByteDeserializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ChatMemberDTO {
    @SerializedName("username")
    private String name;

    @SerializedName("userId")
    private Integer id;

    @SerializedName("avatar")
    @JsonAdapter(ByteDeserializer.class)
    private byte[] image;

    @Setter
    private transient boolean isAlreadyInChat;

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
