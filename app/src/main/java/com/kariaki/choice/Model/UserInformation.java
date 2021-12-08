package com.kariaki.choice.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;

public class UserInformation {


    private String profileImage;
    private String about;
    private String name;
    private String phoneNumber;
    private static final String LIST_OF_USERS="users";
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference usersCollcetion=database.getReference(LIST_OF_USERS);
    private  String userID;
    private String mainUserID;
    private SharedPreferences preferences;
    private final String shareDefaul="no user";
    private final String sharedKey="userDetail";



    private static UserInformation userInformation;

    public static UserInformation getInstance (Context context){
        if(userInformation==null){
            userInformation=new UserInformation(context);
        }
        return userInformation;
    }

    public UserInformation(Context context){

        preferences=context.getSharedPreferences("user",MODE_PRIVATE);
        mainUserID =preferences.getString("phone number",shareDefaul);

    }

    public String getMainUserID() {

        return mainUserID;

    }

    public String getPhoneNumber() {
        return mainUserID;
    }

    public String getAbout() {
        return about;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getName() {
        return name;
    }
}
