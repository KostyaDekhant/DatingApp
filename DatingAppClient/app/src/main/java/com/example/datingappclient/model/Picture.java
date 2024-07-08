package com.example.datingappclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Picture {
    @JsonProperty("image_id")
    private int image_id;
    @JsonProperty("image")
    private byte[] image;
    @JsonProperty("user_id")
    private int user_id;

    public Picture(int image_id, byte[] image, int user_id) {
        this.image_id = image_id;
        this.image = image;
        this.user_id = user_id;
    }

    public Picture() {
        this.image_id = -1;
        this.image = null;
        this.user_id = -1;
    }
}