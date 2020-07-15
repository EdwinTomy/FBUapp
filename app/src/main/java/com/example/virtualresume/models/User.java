package com.example.virtualresume.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.Date;


@ParseClassName("User")
public class User extends ParseUser {

    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_PROFILEIMAGE = "profileImage";
    public static final String KEY_HOME = "home";
    public static final String KEY_BIO = "bio";

    public String getFirstName() {
        return getString(KEY_FIRSTNAME);
    }

    public void setFirstName(String firstName){
        put(KEY_FIRSTNAME, firstName);
    }

    public ParseFile getProfileImage() {
        return getParseFile(KEY_PROFILEIMAGE);
    }

    public void setProfileImage(ParseFile parseFile){
        put(KEY_PROFILEIMAGE, parseFile);
    }

    public String getLastName() {
        return getString(KEY_LASTNAME);
    }

    public void seLastName(String lastName){
        put(KEY_LASTNAME, lastName);
    }

    public ParseGeoPoint getHome() {
        return getParseGeoPoint(KEY_HOME);
    }

    public void setHome(ParseGeoPoint parseGeoPoint){
        put(KEY_HOME, parseGeoPoint);
    }

    public String getBio() {
        return getString(KEY_BIO);
    }

    public void setBio(String bio){
        put(KEY_BIO, bio);
    }
}