package com.kariaki.choice.model.Database.Entities;

public class ContactPost {
    private String contactName;

    private String phoneNumber;
    private boolean mute;
    private boolean blockUser;




    public ContactPost(String contactName, String phoneNumber,boolean mute,boolean blockUser) {
        this.contactName = contactName;

        this.phoneNumber = phoneNumber;
        this.mute=mute;
        this.blockUser=blockUser;
    }

    public ContactPost(){

    }

    public boolean isMute() {
        return mute;
    }

    public boolean isBlockUser() {
        return blockUser;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public void setBlockUser(boolean blockUser) {
        this.blockUser = blockUser;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

}
