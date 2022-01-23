package com.kariaki.choice;

import android.content.Context;

import com.kariaki.choice.model.UserInformation;

public class AccountID {

    private AccountID(Context context){
        String phone=new UserInformation(context).getMainUserID();
        setPhoneNumber(phone);
    }
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private static AccountID instance;
    public static AccountID getInstance(Context context){
        if(instance==null){
            return new AccountID(context);
        }else {
            return instance;
        }
    }
}
