package com.kariaki.choice.ContactSync;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

public class ContactSync {

    private  static  Context CONTEXT;
    private static List<ContactData>data=new ArrayList<>();
    private static List<String>phoneNumber=new ArrayList<>();
    public static List<ContactData> getContacts(Context context){
        CONTEXT=context;
        Cursor cursor=context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);
        while(cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
            .Phone.DISPLAY_NAME));
            String phone=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
            .Phone.NUMBER));
            data.add(new ContactData(name,phone));
        }

        return data;
    }

    public static List<String> getPhoneNumber(Context context){
        CONTEXT=context;
        Cursor cursor=context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);
        while(cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                    .Phone.DISPLAY_NAME));
            String phone=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                    .Phone.NUMBER));
            phoneNumber.add(phone);
        }

        return phoneNumber;
    }
    public static List<String> getContactName(Context context){
        CONTEXT=context;
        Cursor cursor=context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);
        while(cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds
                    .Phone.DISPLAY_NAME));

            phoneNumber.add(name);
        }

        return phoneNumber;
    }
}
