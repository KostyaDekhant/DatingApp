package com.example.datingappclient.model;

public class User {

    public User(int id) {
        this.id = id;
    }

    public User(int id, String username, String desc, String age) {
        this.id = id;
        this.username = username;
        this.desc = desc;
        this.age = age;
    }

    private int id;
    private String username;
    private String desc;
    private String age;

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
}
