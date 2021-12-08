package com.kariaki.choice.UI.GroupChat;

public class GroupMember {
    String memberPhone;
    boolean admin;
    public GroupMember(String memberPhone, boolean admin){
        this.memberPhone=memberPhone;
        this.admin=admin;
    }

    public GroupMember(){

    }

    public boolean isAdmin() {
        return admin;
    }

    public String getMemberPhone() {
        return memberPhone;
    }
}