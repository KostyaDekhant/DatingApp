package com.example.datingappclient;

public class User {

    public User(int id, String username, String desc) {
        this.id = id;
        this.username = username;
        this.desc = desc;
    }

    private int id;
    private String username;
    private String desc;

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
}
