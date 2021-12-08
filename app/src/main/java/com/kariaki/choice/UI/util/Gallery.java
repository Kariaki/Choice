package com.kariaki.choice.UI.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.kariaki.choice.UI.MakePost.GalleryItem;

import java.util.ArrayList;
import java.util.List;

public class Gallery {




    String path = "";
    List<GalleryItem> images = new ArrayList<>();



    private boolean allPermissionGranted(Activity activity,String[]allRequiredPermisions) {

        for (String permission : allRequiredPermisions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void getAllImages(Activity activity) {

        if(!allPermissionGranted(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        }

        Uri uri;
        ArrayList<String> output = new ArrayList<>();
        Cursor cursor;
        int column_index_data, columnFolderName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
          //  uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            uri = MediaStore.Files.getContentUri("external");
        }
        //uri = MediaStore.Files.getContentUri("external");

        String[] projection = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MIME_TYPE,
        };

        String selection ="";
        switch (galleryType){
            case 1:
                selection=MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                break;
            case 2:
                selection=MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        ;
                break;



        }

        cursor = activity.managedQuery(uri, projection, selection, null, MediaStore.MediaColumns.DATE_ADDED);

                /*getContentResolver().query(uri, projection, selection,
                null, MediaStore.MediaColumns.DATE_ADDED);

                 */


        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);

        int type = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);

        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
        bitmapOption.inSampleSize = 4;
        bitmapOption.inPurgeable = true;

        //columnFolderName = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);

        cursor.moveToLast();

        int i = 0;
        while (cursor.moveToPrevious()) {
            i++;

            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID));
            int newMediaType = cursor.getInt(type);

            if (i == cursor.getCount()) {
                break;
            }
            path = cursor.getString(column_index_data);

            switch (newMediaType) {
                case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                    GalleryItem galleryItem = new GalleryItem(path, null, 1, false);

                    images.add(galleryItem);
                    break;
                case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:

                    galleryItem = new GalleryItem(path, null, 2, false);

                    images.add(galleryItem);
                    break;
            }
            //GalleryItem galleryItem = new GalleryItem(path, null, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,false);

            //images.add(galleryItem);


        }
    }

    private Gallery(){

    }
    private static int galleryType=1;

    public Gallery(Activity activity){
        getAllImages(activity);
    }

    public void setGalleryType(int galleryType) {
        this.galleryType = galleryType;
    }

    public List<GalleryItem> getImages() {
        return images;
    }

    public String getPath() {
        return path;
    }
}
