package com.kariaki.choice.model.Database.Entities;

public class NotificationForPost {

    private String userID,tittle,body,messageID,postID,secondUser;
    private long timeStamp;
    private boolean seen;

    public NotificationForPost(String userID, String tittle, String body,long timeStamp,String messageID,String postID,String secondUser,boolean seen) {
        this.userID = userID;
        this.tittle = tittle;
        this.body = body;
        this.timeStamp=timeStamp;
        this.seen=seen;
        this.messageID=messageID;
        this.secondUser=secondUser;
        this.postID=postID;
    }

    public void setSecondUser(String secondUser) {
        this.secondUser = secondUser;
    }

    public String getSecondUser() {
        return secondUser;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
    public NotificationForPost(){

    }

    public boolean isSeen() {
        return seen;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getPostID() {
        return postID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
