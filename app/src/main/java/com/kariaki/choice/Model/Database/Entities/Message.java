package com.kariaki.choice.Model.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kariaki.choice.Model.MessageType;

@Entity(tableName = "messagetable")
public class Message implements MessageType {


    @NonNull
    @PrimaryKey
    private String messageID;
    private String channelID;
    private int messageTYPE;
    private String messageOwnerID;
    private long messageTIME;
    private String messageCONTENT;
    private String messageURL;
    private int channelType;
    private int messageState;



    public Message(){

    }

    public Message(@NonNull String messageID, String channelID, int messageTYPE, String messageOwnerID,
                   long messageTIME,
                   String messageCONTENT, String messageURL, int channelType, int messageState) {

        this.messageTYPE = messageTYPE;
        this.messageOwnerID = messageOwnerID;
        this.messageTIME = messageTIME;
        this.messageCONTENT = messageCONTENT;
        this.messageURL = messageURL;
        this.channelType = channelType;
        this.channelID=channelID;
        this.messageID=messageID;
        this.messageState=messageState;

    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public void setMessageID(@NonNull String messageID) {
        this.messageID = messageID;
    }


    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getChannelID() {
        return channelID;
    }

    @NonNull
    public String getMessageID() {
        return messageID;
    }

    public int getChannelType() {
        return channelType;
    }

     public int getMessageTYPE() {return messageTYPE;}

    public String getMessageOwnerID() {
        return messageOwnerID;
    }

    public long getMessageTIME() {
        return messageTIME;
    }

    public String getMessageCONTENT() {
        return messageCONTENT;
    }

    public String getMessageURL() {
        return messageURL;
    }

    @Override
    public int getMessageType() {
        return messageTYPE;
    }

    public void setMessageTYPE(int messageTYPE) {
        this.messageTYPE = messageTYPE;
    }

    public void setMessageOwnerID(String messageOwnerID) {
        this.messageOwnerID = messageOwnerID;
    }

    public void setMessageTIME(long messageTIME) {
        this.messageTIME = messageTIME;
    }

    public void setMessageCONTENT(String messageCONTENT) {
        this.messageCONTENT = messageCONTENT;
    }

    public void setMessageURL(String messageURL) {
        this.messageURL = messageURL;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }
}
