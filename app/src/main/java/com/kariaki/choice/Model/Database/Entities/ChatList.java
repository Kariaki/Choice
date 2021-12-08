package com.kariaki.choice.Model.Database.Entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chatList")
public class ChatList {

    @NonNull
    @PrimaryKey
    private String channelID;
    private int channelType;
    long messageTIME;

    public String getChannelID() {
        return channelID;
    }
    public ChatList(){


    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public long getMessageTIME() {
        return messageTIME;
    }

    public void setMessageTIME(long messageTIME) {
        this.messageTIME = messageTIME;
    }

    public ChatList(@NonNull String channelID, long messageTIME, int channelType) {
        this.channelType = channelType;
        this.channelID = channelID;
        this.messageTIME = messageTIME;

    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    public int getChannelType() {
        return channelType;
    }
}
