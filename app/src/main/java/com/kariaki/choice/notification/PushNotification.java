package com.kariaki.choice.notification;

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
