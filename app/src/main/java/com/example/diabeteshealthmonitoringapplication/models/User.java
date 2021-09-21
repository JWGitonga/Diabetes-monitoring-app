package com.example.diabeteshealthmonitoringapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
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

    protected User(Parcel in) {
        uid = in.readString();
        username = in.readString();
        email = in.readString();
        phone = in.readString();
        imageUrl = in.readString();
        role = in.readString();
        deviceToken = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(imageUrl);
        dest.writeString(role);
        dest.writeString(deviceToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
