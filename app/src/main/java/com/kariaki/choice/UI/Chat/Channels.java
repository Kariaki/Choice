package com.kariaki.choice.UI.Chat;

public class Channels {

    public Channels(){

    }

    public String id;
    private long lastMessageTime;
    private int channelType;
    private boolean mute;
    private boolean blockUser;

    public String getChatChannelID() {
        return id;
    }

    public Channels(String chatChannelID, long lastMessageTime,
                         int channelType, boolean mute, boolean blockUser) {
        this.id = chatChannelID;
        this.lastMessageTime = lastMessageTime;

        this.channelType=channelType;
        this.mute=mute;
        this.blockUser=blockUser;
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


    public long getLastMessageTime() {
        return lastMessageTime;
    }

}
