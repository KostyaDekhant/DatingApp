package com.example.datingappclient.model;

import android.graphics.Bitmap;

import com.example.datingappclient.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User1
{
    @NonNull
    @JsonProperty("id")
    private int id;
    @NonNull
    @JsonProperty("username")
    private String username;
    @JsonProperty("description")
    private String desc;
    @JsonProperty("birthday")
    private String birthday;
    @Builder.Default
    private List<UserImage> images = new ArrayList<>();
    public User1(int id) {
        this.id = id;
    }
    public int getAge() {
        return DateUtils.dateToAge(birthday);
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
        for (int i = images.size() - 1; i >= 0; i--) {
            if (find) images.get(i).setImageNum(images.get(i).getImageNum() - 1);
            if (!find && images.get(i).getImageNum() == imageNum) {
                images.remove(i);
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
