package com.kariaki.choice.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kariaki.choice.UI.Post.PostTypes;

@Entity(tableName = "post_table")
public class Post implements PostTypes, Parcelable {

    @PrimaryKey
    @NonNull
    private String postID;
    private String POST_CAPTION;
    private String POST_URL;
    private int POST_TYPE;
    private long POST_TIME;
    private boolean repeat;
    private String ownerID;
    private String fromUserID;
    private boolean allowPrivateComment;


    public Post(@NonNull String POST_ID, String ownerID, String POST_CAPTION, String POST_URL,
                int POST_TYPE, long POST_TIME,  boolean repeat, String fromUserID, boolean allowPrivateComment) {

        this.POST_CAPTION = POST_CAPTION;
        this.POST_URL = POST_URL;
        this.POST_TYPE = POST_TYPE;
        this.postID=POST_ID;
        this.repeat=repeat;
        this.POST_TIME=POST_TIME;
        this.fromUserID=fromUserID;
        this.ownerID=ownerID;
        this.allowPrivateComment=allowPrivateComment;

    }

    public void setAllowPrivateComment(boolean allowPrivateComment) {
        this.allowPrivateComment = allowPrivateComment;
    }

    public boolean isAllowPrivateComment() {
        return allowPrivateComment;
    }

    protected Post(Parcel in) {
        postID = in.readString();
        POST_CAPTION = in.readString();
        POST_URL = in.readString();
        POST_TYPE = in.readInt();
        POST_TIME = in.readLong();
        repeat = in.readByte() != 0;
        ownerID = in.readString();
        fromUserID = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public void setFromUserID(String fromUserID) {
        this.fromUserID = fromUserID;
    }

    public String getFromUserID() {
        return fromUserID;
    }


    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setPOST_CAPTION(String POST_CAPTION) {
        this.POST_CAPTION = POST_CAPTION;
    }

    public void setPOST_URL(String POST_URL) {
        this.POST_URL = POST_URL;
    }

    public void setPOST_TYPE(int POST_TYPE) {
        this.POST_TYPE = POST_TYPE;
    }

    public void setPOST_TIME(long POST_TIME) {
        this.POST_TIME = POST_TIME;
    }


    @Override
    public int getPostType() {
        return POST_TYPE;
    }


    public int getPOST_TYPE() {
        return POST_TYPE;
    }

    public String getPOST_CAPTION() {
        return POST_CAPTION;
    }

    public String getPOST_URL() {
        return POST_URL;
    }

    public long getPOST_TIME() {
        return POST_TIME;
    }

    public Post(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postID);
        dest.writeString(POST_CAPTION);
        dest.writeString(POST_URL);
        dest.writeInt(POST_TYPE);
        dest.writeLong(POST_TIME);
        dest.writeByte((byte) (repeat ? 1 : 0));
        dest.writeString(ownerID);
        dest.writeString(fromUserID);
    }
}
