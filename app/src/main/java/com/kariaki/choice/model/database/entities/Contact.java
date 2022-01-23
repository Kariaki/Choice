package com.kariaki.choice.model.database.entities;

public class Contact {
    private String contactName;

    private String phoneNumber;


    public Contact(String contactName, String phoneNumber) {
        this.contactName = contactName;

        this.phoneNumber = phoneNumber;
    }

    public Contact(){

    }


    public void setContactName(String contactName) {
        this.contactName = contactName;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

}
