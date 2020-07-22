package com.example.virtualresume.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;


@Parcel(analyze = {Following.class})
@ParseClassName("Following")
public class Following extends ParseObject {

    public static final String FOLLOWING_KEY_USER = "user";
    public static final String FOLLOWING_KEY_FOLLOWS = "follows";

    public ParseUser getFollowingUser() {
        return getParseUser(FOLLOWING_KEY_USER);
    }

    public ParseUser getFollowingFollows() {
        return getParseUser(FOLLOWING_KEY_FOLLOWS);
    }

    public void setFollowingUser(ParseUser user) {
        put(FOLLOWING_KEY_USER, user);
    }

    public void setFollowingFollows(ParseUser user) {
        put(FOLLOWING_KEY_FOLLOWS, user);
    }
}
