package com.kariaki.choice;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class ImageExtractor {


    public static List<String> getAllImage(Context context){


        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<String>listOfImages=new ArrayList<>();
        String imagePath;
        uri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[]projection={MediaStore.MediaColumns.DATA,MediaStore.MediaColumns.BUCKET_DISPLAY_NAME};
        String orderBy=MediaStore.Video.Media.DATE_TAKEN;
        cursor=context.getContentResolver().query(uri,projection,null,null,orderBy+" DESC");
        column_index_data=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()){
            imagePath=cursor.getString(column_index_data);
            listOfImages.add(imagePath);
        }
        return listOfImages;
    }
}
