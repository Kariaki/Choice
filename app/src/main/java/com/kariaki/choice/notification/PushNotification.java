package com.kariaki.choice.Notification;

public class PushNotification {

    private boolean inChat;

    public void setInChat(boolean inChat) {
        this.inChat = inChat;
    }

    public PushNotification(boolean inChat) {
        this.inChat = inChat;
    }

    public PushNotification() {

    }

    public boolean isInChat() {
        return inChat;
    }
}
