package com.example.datingappclient.model.dto;

import android.graphics.Bitmap;

import com.example.datingappclient.model.UserImage;
import com.example.datingappclient.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO
{
    @JsonProperty("id")
    private int id;
    @NonNull
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    //@Positive
    @JsonProperty("height")
    private int height;
    //@NotBlank
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("is_online")
    private Boolean isOnline;
    @JsonProperty("last_online")
    private Timestamp lastOnline;

    @JsonIgnore
    transient private List<UserImage> images = new ArrayList<>();

    @JsonIgnore
    transient private CompanyInfoDTO companyInfo = new CompanyInfoDTO();

    @Override
    public String toString() {
        return "User  {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", birthday=" + birthday +
                ", height=" + height +
                ", gender='" + gender + '\'' +
                ", is_online=" + isOnline +
                ", last_online=" + lastOnline +
                '}';
    }

    public UserDTO(int id) {
        this.id = id;
    }

    public UserDTO(int id, String name, LocalDate birthday, String gender) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
    }

    public void copyFrom(UserDTO other) {
        this.id = other.id;
        this.name = other.name;
        this.birthday = other.birthday;
        this.description = other.description;
        this.height = other.height;
        this.gender = other.gender;
        this.isOnline = other.isOnline;
        this.lastOnline = other.lastOnline;
        // и т.д.
    }

    public int getAge() {
        return DateUtils.dateToAge(birthday);
    }

    public void setListImages(List<UserImage> images) {
        this.images = images;

        // если изображений нет, то выводим дефолтное (возвращается с сервера)
        // TODO: изображение по умолчанию можно хранить на клиенте, чтобы не гонять туда-сюда
        sortImages();
    }

    public int getListImagesSize() {return images.size();}

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
        if (images == null || images.isEmpty()) return null;
        for (UserImage it : images) {
            if (it.getImageNum() == 1) return it.getImage();
        }
        return null;
    }

    private void sortImages() {
        images.sort(Comparator.comparingInt(u -> u.getImageNum()));
    }
}
