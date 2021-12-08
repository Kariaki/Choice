package com.kariaki.choice.Model.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chatAndPostNotificationTable")

public class ChatAndPostNotification {

    @NonNull
    @PrimaryKey
    String notificationID;
    int notificationType;

    public ChatAndPostNotification(@NonNull String notificationID, int notificationType) {
        this.notificationID = notificationID;
        this.notificationType = notificationType;
    }

    @NonNull
    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(@NonNull String notificationID) {
        this.notificationID = notificationID;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }
}
