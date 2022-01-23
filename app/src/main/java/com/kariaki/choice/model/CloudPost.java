package com.kariaki.choice.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kariaki.choice.ui.post.PostTypes;

@Entity(tableName = "my_post_table")
public class CloudPost implements PostTypes, Parcelable {


    @PrimaryKey
    @NonNull
    private String postID;
    private String POST_CAPTION;
    private String POST_URL;
    private int POST_TYPE;
    private long POST_TIME;
    private String ownerID;

   private long POST_LIFE_SPAN;
   private String IS_ON_REPEAT;


    public CloudPost(String POST_ID, String POST_OWNER_ID,
                     String POST_CAPTION, String POST_URL,
                     int POST_TYPE, long  POST_TIME, long POST_LIFE_SPAN, String IS_ON_REPEAT) {
        this.POST_CAPTION = POST_CAPTION;
        this.POST_URL = POST_URL;
        this.POST_TYPE = POST_TYPE;
        this.POST_TIME=POST_TIME;
        this.IS_ON_REPEAT=IS_ON_REPEAT;
        this.POST_LIFE_SPAN=POST_LIFE_SPAN;
        this.ownerID=POST_OWNER_ID;
        this.postID=POST_ID;
    }



    public void setPOST_LIFE_SPAN(long POST_LIFE_SPAN) {
        this.POST_LIFE_SPAN = POST_LIFE_SPAN;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public void setIS_ON_REPEAT(String IS_ON_REPEAT) {
        this.IS_ON_REPEAT = IS_ON_REPEAT;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    protected CloudPost(Parcel in) {

        POST_CAPTION = in.readString();
        POST_URL = in.readString();
        POST_TYPE = in.readInt();
       POST_TIME = in.readLong();
        ownerID=in.readString();
        postID=in.readString();
        POST_LIFE_SPAN=in.readLong();
        IS_ON_REPEAT=in.readString();
    }

    public static final Creator<CloudPost> CREATOR = new Creator<CloudPost>() {
        @Override
        public CloudPost createFromParcel(Parcel in) {
            return new CloudPost(in);
        }


        @Override
        public CloudPost[] newArray(int size) {
            return new CloudPost[size];
        }
    };


    public long getPOST_LIFE_SPAN() {
        return POST_LIFE_SPAN;
    }

    public String getIS_ON_REPEAT() {
        return IS_ON_REPEAT;
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


    public String getPostID() {
        return postID;
    }

    public CloudPost(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(POST_CAPTION);
        dest.writeString(POST_URL);
        dest.writeInt(POST_TYPE);
       dest.writeLong(POST_TIME);
        dest.writeString(ownerID);
        dest.writeString(postID);
        dest.writeLong(POST_LIFE_SPAN);
        dest.writeString(IS_ON_REPEAT);
    }

}

