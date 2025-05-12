package com.example.datingappclient.model.dto;

import com.example.datingappclient.utils.gson.ByteDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeDTO {
    @SerializedName("liker")
    private int likerId;

    @SerializedName("poster")
    private int posterId;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    @JsonAdapter(ByteDeserializer.class)
    private byte[] image;

    @SerializedName("birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    public LikeDTO(int likerId, int posterId) {
        this.likerId = likerId;
        this.posterId = posterId;
    }
}
