package com.thilojaeggi.frooze;


import android.app.Application;

import android.os.Bundle;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePlugins;


public class App extends Application {


    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
            .applicationId("7eJ0VAwv0amaiJQyBLTou3uEXlAALFi7irMoRN23")
                .clientKey("LRWcdQoOtccLZrzDwIThvc0Ih1tdgLFDYKWmMhkK")
                .server("https://parseapi.back4app.com/")
                .build());
                ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}

