package com.socialone.android.utils;

import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Datastore {

    private static final String SEEN_START_UP = "Startup";
    private static final String LOGGED_IN_TO_FACEBOOK = "LoggedInToFacebook";
    private static final String LOGGED_IN_TO_TWITTER = "LoggedInToTwitter";
    private static final String LOGGED_IN_TO_PLUS = "LoggedInToPlus";
    private static final String LOGGED_IN_TO_FOUR = "LoggedInToPlus";
    private static final String LOGGED_IN_TO_INSTAGRAM = "LoggedInToInstagram";
    private static final String TWITTER_ACCESS_KEY = "twitter_access_key";
    private static final String TWITTER_ACCESS_SECRET = "twitter_access_secret";

    private static final String INSTAGRAM_ACCESS_KEY = "instagram_access_key";
    private static final String INSTAGRAM_ACCESS_SECRET = "instagram_access_secret";

    private static final String DEVICE_VERSION = "DeviceVersion";

    @Inject
    EncryptedSharedPreferences encryptedSharedPreferences;

    private SharedPreferences.Editor getEditor() {
        return encryptedSharedPreferences.edit();
    }

    private SharedPreferences getPrefs() {
        return encryptedSharedPreferences;
    }
    public int getVersion() {
        return getPrefs().getInt(DEVICE_VERSION, 0);
    }
    public void persistVersion(int version) {
        getEditor().putInt(DEVICE_VERSION, version).commit();
    }

    public boolean getUserSeenStartup() {
        return getPrefs().getBoolean(SEEN_START_UP, false);
    }

    public void setSeenStartUp(boolean loggedIn) {
        getEditor().putBoolean(SEEN_START_UP, loggedIn).commit();
    }

    public boolean getUserLoggedInToFacebook() {
        return getPrefs().getBoolean(LOGGED_IN_TO_FACEBOOK, false);
    }

    public void setUserLoggedInToFacebook(boolean loggedIn) {
        getEditor().putBoolean(LOGGED_IN_TO_FACEBOOK, loggedIn).commit();
    }

    public boolean getUserLoggedInToTwitter() {
        return getPrefs().getBoolean(LOGGED_IN_TO_TWITTER, false);
    }

    public void setUserLoggedInToTwitter(boolean loggedIn) {
        getEditor().putBoolean(LOGGED_IN_TO_TWITTER, loggedIn).commit();
    }


    public boolean getUserLoggedInToPlus() {
        return getPrefs().getBoolean(LOGGED_IN_TO_PLUS, false);
    }

    public void setUserLoggedInToPlus(boolean loggedIn) {
        getEditor().putBoolean(LOGGED_IN_TO_PLUS, loggedIn).commit();
    }

    public boolean getUserLoggedInToFour() {
        return getPrefs().getBoolean(LOGGED_IN_TO_FOUR, false);
    }

    public void setUserLoggedInToFour(boolean loggedIn) {
        getEditor().putBoolean(LOGGED_IN_TO_FOUR, loggedIn).commit();
    }

}

