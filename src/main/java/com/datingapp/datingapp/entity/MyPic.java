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
    @Column(name = "image_id")
    private int image_id;
    @Column(name = "image")
    private byte[] image;
    @JsonProperty("user_id")
    private int user_id;

    public MyPic(int image_id, byte[] image, int user_id) {
        this.image_id = image_id;
        this.image = image;
        this.user_id = user_id;
    }

    public MyPic() {
        this.image_id = -1;
        this.image = null;
        this.user_id = -1;
    }
}
