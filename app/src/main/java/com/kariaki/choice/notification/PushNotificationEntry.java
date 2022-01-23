package com.kariaki.choice.notification;

public class PushNotificationEntry {
    public PushNotificationEntry(){

    }
    private String chatID;
    private int chatType;

    public PushNotificationEntry(String chatID, int chatType) {
        this.chatID = chatID;
        this.chatType = chatType;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }
}
