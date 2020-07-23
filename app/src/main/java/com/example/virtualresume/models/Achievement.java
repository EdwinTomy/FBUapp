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

    public static final String ACHIEVEMENT_KEY_DESCRIPTION = "description";
    public static final String ACHIEVEMENT_KEY_IMAGE = "image";
    public static final String ACHIEVEMENT_KEY_TITLE = "title";
    public static final String ACHIEVEMENT_KEY_FIELD = "field";
    public static final String ACHIEVEMENT_KEY_ORGANIZATION = "organization";
    public static final String ACHIEVEMENT_KEY_TIME = "time";
    public static final String ACHIEVEMENT_KEY_USER = "user";

    public Achievement() {
    }

    public String getAchievementDescription() {
        return getString(ACHIEVEMENT_KEY_DESCRIPTION);
    }

    public void setAchievementDescription(String description){
        put(ACHIEVEMENT_KEY_DESCRIPTION, description);
    }

    public ParseFile getAchievementImage() {
        return getParseFile(ACHIEVEMENT_KEY_IMAGE);
    }

    public void setAchievementImage(ParseFile parseFile){
        put(ACHIEVEMENT_KEY_IMAGE, parseFile);
    }

    public String getAchievementTitle() {
        return getString(ACHIEVEMENT_KEY_TITLE);
    }

    public void setAchievementTitle(String title){
        put(ACHIEVEMENT_KEY_TITLE, title);
    }

    public String getAchievementField() {
        return getString(ACHIEVEMENT_KEY_FIELD);
    }

    public void setAchievementField(String field){
        put(ACHIEVEMENT_KEY_FIELD, field);
    }

    public String getAchievementOrganization() {
        return getString(ACHIEVEMENT_KEY_ORGANIZATION);
    }

    public void setAchievementOrganization(String organization){
        put(ACHIEVEMENT_KEY_ORGANIZATION, organization);
    }

    public Date getTime() {
        return getDate(ACHIEVEMENT_KEY_TIME);
    }

    public void setTime(Date date){
        put(ACHIEVEMENT_KEY_TIME, date);
    }

    public ParseUser getAchievementUser() {
        return getParseUser(ACHIEVEMENT_KEY_USER);
    }

    public void setAchievementUser(ParseUser user){
        put(ACHIEVEMENT_KEY_USER, user);
    }

}
