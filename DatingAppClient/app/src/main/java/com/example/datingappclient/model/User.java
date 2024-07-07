package com.example.datingappclient.model;

import android.graphics.Bitmap;

import java.util.List;

public class User {


    private int id;
    private String username;
    private String desc;
    private String age;

    private List<UserImage> images;

    public User(int id) {
        this.id = id;
    }

    public User(int id, String username, String desc, String age) {
        this.id = id;
        this.username = username;
        this.desc = desc;
        this.age = age;
    }

    public User(int id, String username, String desc, String age, List<UserImage> userImages) {
        this.id = id;
        this.username = username;
        this.desc = desc;
        this.age = age;
        this.images = userImages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public List<UserImage> getListImages() {
        return images;
    }

    public void setImages(List<UserImage> images) {
        this.images = images;
    }

    public Bitmap getMainImage() {
        for (UserImage it : images) {
            if (it.getImageNum() == 1) return it.getImage();
        }
        return null;
    }
}
