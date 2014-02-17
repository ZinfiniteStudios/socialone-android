package com.socialone.android.tools;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.socialone.android.MainApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by david.hodge on 2/4/14.
 */
public class TumblrUser {
    private static TumblrUser currentUser;
    protected JSONObject jsonObject;

    static SharedPreferences prefs;
    public String getBlogHostname() {
        try {
            JSONArray blogs = jsonObject.getJSONArray("blogs");
            JSONObject blog = (JSONObject)blogs.get(0);
            return blog.getString("name") + ".tumblr.com";
        } catch (Exception e) {
            return null;
        }
    }

    public static void setCurrentUser(TumblrUser user) {
        prefs = PreferenceManager.getDefaultSharedPreferences(MainApp.getContext());
        currentUser = user;

        if (user == null) {
            prefs.edit().remove("current_user")
                    .commit();
        } else {
            prefs.edit()
                    .putString("current_user", user.jsonObject.toString()).commit();
        }
    }

    public static TumblrUser currentUser() {
        prefs = PreferenceManager.getDefaultSharedPreferences(MainApp.getContext());
        if (currentUser == null) {
            // Attempt to retrieve the current user from shared preferences
            String userJsonString = prefs
                    .getString("current_user", null);
            Log.d("DEBUG", "current_user: " + userJsonString);
            if (userJsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(userJsonString);
                    if (jsonObject != null) {
                        currentUser = TumblrUser.fromJson(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return currentUser;
    }

    public static TumblrUser fromJson(JSONObject jsonObject) {
        TumblrUser user = new TumblrUser();
        user.jsonObject = jsonObject;
        return user;
    }
}
