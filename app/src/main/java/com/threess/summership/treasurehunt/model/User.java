package com.threess.summership.treasurehunt.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("username")
    private String mUserName;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("profile_picture")
    private String mProfilePicture;

    @SerializedName("score")
    private long mScore;

    public User(String userName, String password) {
        this.mUserName = userName;
        this.mPassword = password;
    }

    public String getProfilePicture() {
        return mProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.mProfilePicture = profilePicture;
    }

    public long getScore() {
        return mScore;
    }

    public void setScore(Integer score) {
        this.mScore = score;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }
}
