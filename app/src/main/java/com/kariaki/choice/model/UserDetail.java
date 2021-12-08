package com.kariaki.choice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDetail implements Parcelable {

    private String username,phone,about,profileURL,displayName;

    public UserDetail(String username, String phone, String about, String profileURL,String displayName) {
        this.username = username;
        this.phone = phone;
        this.about = about;
        this.profileURL = profileURL;
        this.displayName=displayName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    protected UserDetail(Parcel in) {
        username = in.readString();
        phone = in.readString();
        about = in.readString();
        profileURL = in.readString();
        displayName=in.readString();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static final Creator<UserDetail> CREATOR = new Creator<UserDetail>() {
        @Override
        public UserDetail createFromParcel(Parcel in) {
            return new UserDetail(in);
        }

        @Override
        public UserDetail[] newArray(int size) {
            return new UserDetail[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public UserDetail(){}
    public String getPhone() {
        return phone;
    }

    public String getAbout() {
        return about;
    }

    public String getProfileURL() {
        return profileURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(about);
        dest.writeString(profileURL);
        dest.writeString(displayName);

    }


}
