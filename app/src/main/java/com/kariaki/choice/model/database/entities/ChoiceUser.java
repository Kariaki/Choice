package com.kariaki.choice.model.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usertable")
public class ChoiceUser {

    @NonNull
    @PrimaryKey
    private String userPhoneNumber;


    private String userName;
    private String userContactName;
    private String userImageUrl;
    private String userAboutMe;

    public void setUserPhoneNumber(@NonNull String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public ChoiceUser(){

    }
    public ChoiceUser(String userName, String userPhoneNumber, String userContactName, String userAboutMe, String userImageUrl) {

        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userContactName = userContactName;
        this.userAboutMe=userAboutMe;
        this.userImageUrl=userImageUrl;

    }

    public String getUserContactName() {
        return userContactName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public void setUserContactName(String userContactName) {
        this.userContactName = userContactName;
    }

    public void setUserAboutMe(String userAboutMe) {
        this.userAboutMe = userAboutMe;
    }

    public String getUserAboutMe() {
        return userAboutMe;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

}
