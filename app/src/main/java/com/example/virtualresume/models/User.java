package com.example.virtualresume.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;


@ParseClassName("User")
public class User extends ParseUser {

    public static final String USER_KEY_FULLNAME = "fullName";
    public static final String USER_KEY_USERNAME = "username";
    public static final String USER_KEY_PROFILEIMAGE = "profileImage";
    public static final String USER_KEY_HOME = "home";
    public static final String USER_KEY_BIO = "bio";
    public static final String USER_KEY_CONTACTS = "friends";

    public User() {
    }

    public String getUserFullName() {
        return getString(USER_KEY_FULLNAME);
    }

    public void setUserFullName(String fullName){
        put(USER_KEY_FULLNAME, fullName);
    }

    public String getUserUsername() {
        return getString(USER_KEY_USERNAME);
    }

    public void setUserUsername(String username){
        put(USER_KEY_USERNAME, username);
    }

    public ParseFile getUserProfileImage() {
        return getParseFile(USER_KEY_PROFILEIMAGE);
    }

    public void setUserProfileImage(ParseFile parseFile){
        put(USER_KEY_PROFILEIMAGE, parseFile);
    }

    public ParseGeoPoint getUserHome() {
        return getParseGeoPoint(USER_KEY_HOME);
    }

    public void setUserHome(ParseGeoPoint parseGeoPoint){
        put(USER_KEY_HOME, parseGeoPoint);
    }

    public String getUserBio() {
        return getString(USER_KEY_BIO);
    }

    public void setUserBio(String bio){
        put(USER_KEY_BIO, bio);
    }

    public ParseRelation getUserContacts(){
        return getRelation(USER_KEY_CONTACTS);
    }

    public void setUserContacts(ParseRelation contacts){
        put(USER_KEY_CONTACTS, contacts);
    }
}
