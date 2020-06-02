package com.example.preventionapp;

public class User {
    private String nickname;
    private String name;
    private String residenceEdit;
    private String phoneNumber;
    private String gender;

    public User(String nickname, String name, String residenceEdit, String phoneNumber, String gender) {
        this.nickname = nickname;
        this.name = name;
        this.residenceEdit = residenceEdit;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public String getName() {
        return name;
    }

    public String getResidenceEdit() {
        return residenceEdit;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }
}
