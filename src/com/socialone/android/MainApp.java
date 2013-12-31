package com.socialone.android;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crittercism.app.Crittercism;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;
import com.socialone.android.activity.StartupActivity;
import com.socialone.android.utils.Datastore;

import roboguice.RoboGuice;

public class MainApp extends Application {

    public static String TAG = "socialone-android";
    public static final String XMLNS = "http://socialone-android/schema";

    @Inject
    Datastore mDataStore;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        Injector i = RoboGuice.getBaseApplicationInjector(this);
        Crashlytics.start(this);
        Crittercism.init(getApplicationContext(), getResources().getString(R.string.critter_id));
        Parse.initialize(this, "qAC53f5OOSOSrWNS5rSPzqZRSyZdfBxbvLQg1zFH", "2DcrC6RA6a3zZ1HKgKKZmjf37aEUWiNGEWpY2cda");
        PushService.setDefaultPushCallback(this, StartupActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        mDataStore = i.getInstance(Datastore.class);
        try {
            int newVersionCode = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionCode;
            int oldVersionCode = mDataStore.getVersion();
            if (oldVersionCode != 0 && oldVersionCode != newVersionCode) {
                onVersionUpdate(oldVersionCode, newVersionCode);
            }
            mDataStore.persistVersion(newVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onVersionUpdate(int oldVersionCode, int newVersionCode) {
        //this method is called when the version code changes, use comparison operators to control migration
    }
}

