package com.tiham_dipta.saloonapp.models;

import java.io.Serializable;

public class User implements Serializable {

    private String displayName;
    private String email;
    private String userType;

    public User(String displayName, String email, String userType) {
        this.displayName = displayName;
        this.email = email;
        this.userType = userType;
    }

    public User() {

    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
