package com.kariaki.choice.UI.GroupChat.Adapter;

public class GroupChatMember {
    private String phoneNumber;
    private boolean isAdmin;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public GroupChatMember(String phoneNumber, boolean isAdmin) {
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
