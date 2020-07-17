package com.example.virtualresume.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.security.KeyFactory;
import java.security.KeyStore;
import java.util.Date;

@Parcel(analyze = {Achievement.class})
@ParseClassName("Achievement")
public class Achievement extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TITLE = "title";
    public static final String KEY_FIELD = "field";
    public static final String KEY_ORGANIZATION = "organization";
    public static final String KEY_TIME = "time";
    public static final String KEY_USER = "user";

    public Achievement() {
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void seTitle(String title){
        put(KEY_TITLE, title);
    }

    public String getField() {
        return getString(KEY_FIELD);
    }

    public void setField(String field){
        put(KEY_FIELD, field);
    }

    public String getOrganization() {
        return getString(KEY_ORGANIZATION);
    }

    public void setOrganization(String organization){
        put(KEY_ORGANIZATION, organization);
    }

    public Date getTime() {
        return getDate(KEY_TIME);
    }

    public void setTime(Date date){
        put(KEY_TIME, date);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

}
