package com.kariaki.choice.Model.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kariaki.choice.Model.Database.ChoiceDatabase;
import com.kariaki.choice.UI.Chat.Channels;

@Entity(tableName = "channels")
public class MyChatChannel {

    public MyChatChannel(){
    }

    @NonNull
    @PrimaryKey
    private String chatID;
    private String id;
    private long lastMessageTime;
    private int channelType;
    private boolean mute;
    private boolean blockUser;
    private String name,profileUrl,messageContent,messageOwnerID;
    int messageType;
    int messageState,badgeCount;

    public String getId() {
        return id;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public MyChatChannel(String chatID, String id, long lastMessageTime,
                         int channelType, boolean mute, boolean blockUser, String name, String profileUrl
    , String messageContent, String messageOwnerID, int messageType, int messageState, int badgeCount) {
        this.id = id;
        this.lastMessageTime = lastMessageTime;
        this.badgeCount=badgeCount;
        this.messageState=messageState;
        this.messageContent=messageContent;
        this.messageOwnerID=messageOwnerID;
        this.messageType=messageType;

        this.channelType=channelType;
        this.mute=mute;
        this.blockUser=blockUser;
        this.chatID=chatID;
        this.name=name;
        this.profileUrl=profileUrl;

    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public int getMessageState() {
        return messageState;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageOwnerID() {
        return messageOwnerID;
    }

    public void setMessageOwnerID(String messageOwnerID) {
        this.messageOwnerID = messageOwnerID;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getChatID() {
        return chatID;
    }

    public boolean isMute() {
        return mute;
    }

    public void setBlockUser(boolean blockUser) {
        this.blockUser = blockUser;
    }

    public boolean isBlockUser() {
        return blockUser;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public int getChannelType() {
        return channelType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }


    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

}
