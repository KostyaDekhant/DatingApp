package com.example.datingappclient.model;

import android.graphics.Bitmap;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UserImage {

    @Setter
    private int imageNum;
    @Setter
    private int imageID;
    private final Bitmap image;

    public  UserImage() {
        imageNum = 0;
        image = null;
    }
    public UserImage(int imageNum, int imageID, Bitmap image) {
        this.imageNum = imageNum;
        this.imageID = imageID;
        this.image = image;
    }

}
