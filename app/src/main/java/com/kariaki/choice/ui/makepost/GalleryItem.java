package com.kariaki.choice.ui.makepost;

import android.graphics.Bitmap;

public class GalleryItem {

    String fileURL;
    int fileType;
    Bitmap bitmap;
    boolean isMarked;

    public GalleryItem(String fileURL, Bitmap bitmap, int fileType, boolean isMarked) {
        this.fileURL = fileURL;
        this.fileType = fileType;
        this.bitmap=bitmap;
        this.isMarked=isMarked;
    }

    public String getFileURL() {
        return fileURL;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getFileType() {
        return fileType;
    }

    public boolean isMarked() {
        return isMarked;
    }
}
