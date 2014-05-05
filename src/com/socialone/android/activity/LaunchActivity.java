package com.socialone.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.socialone.android.fragment.SocialConnectFragment;
import com.socialone.android.utils.Datastore;

/**
 * Created by david.hodge on 1/7/14.
 */
public class LaunchActivity extends SherlockActivity {

    Datastore datastore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        datastore = new Datastore();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LaunchActivity.this);
//        Boolean facebook = pre
        Log.d("prefs", Boolean.toString(prefs.getBoolean("facebook", false)));

        //TODO check all networks and only go to login screen if none are signed in
        if(prefs.getBoolean("facebook", false) == true || prefs.getBoolean("appnet", false) == true || prefs.getBoolean("foursquare", false) == true){
            startActivity(new Intent(LaunchActivity.this, MainActivity.class));
        }else{
            startActivity(new Intent(LaunchActivity.this, SocialConnectFragment.class));
        }
    }
}
