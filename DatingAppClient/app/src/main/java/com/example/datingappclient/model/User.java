package com.example.datingappclient.model;

import android.graphics.Bitmap;

import java.util.Comparator;
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
        sortImages();
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

    public void setListImages(List<UserImage> images) {
        this.images = images;
        sortImages();
    }

    public void addUserImage(UserImage image) {
        images.add(image);
    }

    public void setUserImageID(int imageID, int imageNum) {
        if (images.isEmpty()) return;
        for (UserImage it : images) {
            if (it.getImageNum() == imageNum) it.setImageID(imageID);
        }
    }

    public int getUserImageID(int imageNum) {
        for (UserImage it : images) {
            if (it.getImageNum() == imageNum) return it.getImageID();
        }
        return 0;
    }

    public void removeImage(int imageNum) {
        boolean find = false;
        for (UserImage it : images) {
            if (find) it.setImageNum(it.getImageNum() - 1);
            if (it.getImageNum() == imageNum) {
                images.remove(it);
                find = true;
            }
        }
    }

    public Bitmap getMainImage() {
        if (images.isEmpty()) return null;
        for (UserImage it : images) {
            if (it.getImageNum() == 1) return it.getImage();
        }
        return null;
    }

    private void sortImages() {
        images.sort(Comparator.comparingInt(u -> u.getImageNum()));
    }
}
