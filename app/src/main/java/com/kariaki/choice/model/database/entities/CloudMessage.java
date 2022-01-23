package com.kariaki.choice.model.database.entities;

public class CloudMessage {

    private String messageID;
    private int messageTYPE;
    private String messageOwnerID;
    private long messageTIME;
    private String messageCONTENT;
    private String messageURL;
    private int messageState;


    public CloudMessage(int messageTYPE, String messageOwnerID, long messageTIME,
                        String messageCONTENT, String messageURL,int messageState) {
        this.messageTYPE = messageTYPE;
        this.messageOwnerID = messageOwnerID;
        this.messageTIME = messageTIME;
        this.messageCONTENT = messageCONTENT;
        this.messageURL = messageURL;
        this.messageState=messageState;


    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
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

    public CloudMessage(){}
}
