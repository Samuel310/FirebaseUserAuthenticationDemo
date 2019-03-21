package com.myfirebasedemo.samuel.firebaseuserauthenticationdemo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserDatabaseModel {
    private String name;
    private String email;
    private String phoneNo;

    //test-audit Allwyn
    public UserDatabaseModel(){
    }

    public UserDatabaseModel(String name, String email, String phoneNo) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
