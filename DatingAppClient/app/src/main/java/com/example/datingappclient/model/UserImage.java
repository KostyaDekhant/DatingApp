package com.example.datingappclient.model;

import android.graphics.Bitmap;

public class UserImage {

    private int imageNum;
    private int imageID;
    private Bitmap image;

    public  UserImage() {
        imageNum = 0;
        image = null;
    }
    public UserImage(int imageNum, int imageID, Bitmap image) {
        this.imageNum = imageNum;
        this.imageID = imageID;
        this.image = image;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
