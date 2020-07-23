package com.example.virtualresume.network;

import android.app.Application;

import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Register parse models
        ParseObject.registerSubclass(Achievement.class);
        ParseObject.registerSubclass(User.class);

        //Heroku server accessed
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("fbuappvirtualresume") //APP_ID env variable
                .clientKey(null)
                .server("https://fbuappvirtualresume.herokuapp.com/parse/").build());
    }
}
