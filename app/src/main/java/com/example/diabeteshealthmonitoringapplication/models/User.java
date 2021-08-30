package com.example.diabeteshealthmonitoringapplication.models;

public class User {
    private String uid,username,email,phone,imageUrl,role,deviceToken;

    /***
     * Default empty constructor for firebase
     * with uid @Param - Firebase Assign userId
     */
    public User() {

    }

    public User(String uid, String username, String email, String phone,String imageUrl,String role,String deviceToken) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.role = role;
        this.deviceToken = deviceToken;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
