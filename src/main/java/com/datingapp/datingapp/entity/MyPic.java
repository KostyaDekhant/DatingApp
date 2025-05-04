package com.datingapp.datingapp.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MyPic {
    @JsonProperty("image_id")
    private int imageId;
    @JsonProperty("image")
    private byte[] image;
    @JsonProperty("user_id")
    private int userId;

    public MyPic(int imageId, byte[] image, int userId) {
        this.imageId = imageId;
        this.image = image;
        this.userId = userId;
    }

    public MyPic() {
        this.imageId = -1;
        this.image = null;
        this.userId = -1;
    }
}
