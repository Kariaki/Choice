package com.kariaki.choice.UI.GroupChat;

public class GroupChatInformation {

    private String creator;
    private long time_created;
    private String imageUrl;
    private String channelKey;
    private String description;
    private String groupChatName;

    public GroupChatInformation(){

    }

    public GroupChatInformation(String creator, long time_created, String imageUrl, String channelKey, String groupChatName, String description) {
        this.creator = creator;
        this.time_created = time_created;
        this.imageUrl = imageUrl;
        this.channelKey=channelKey;
        this.groupChatName=groupChatName;
        this.description=description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setGroupChatName(String groupChatName) {
        this.groupChatName = groupChatName;
    }

    public String getGroupChatName() {
        return groupChatName;
    }

    public String getChannelKey() {
        return channelKey;
    }

    public String getCreator() {
        return creator;
    }

    public long getTime_created() {
        return time_created;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
