package com.example.datingappclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PictureDTO {
    @SerializedName("image_id")
    private int imageId;
    @JsonProperty("image")
    private byte[] image;
    @SerializedName("user_id")
    private int userId;

    public PictureDTO(int imageId, byte[] image, int userId) {
        this.imageId = imageId;
        this.image = image;
        this.userId = userId;
    }
}