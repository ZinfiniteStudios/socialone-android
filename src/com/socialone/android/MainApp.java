package com.socialone.android;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.socialone.android.utils.Datastore;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainApp extends Application {

    public static String TAG = "socialone-android";
    public static final String XMLNS = "http://socialone-android/schema";

    Datastore mDataStore;
    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        Crashlytics.start(this);
//        Parse.initialize(this, "qAC53f5OOSOSrWNS5rSPzqZRSyZdfBxbvLQg1zFH", "2DcrC6RA6a3zZ1HKgKKZmjf37aEUWiNGEWpY2cda");
//        PushService.setDefaultPushCallback(this, MainActivity.class);
//        ParseInstallation.getCurrentInstallation().saveInBackground();
        Config config = new Config("socialone.uservoice.com");
        UserVoice.init(config, this);
        mContext = getApplicationContext();
//        try {
//            int newVersionCode = getPackageManager()
//                    .getPackageInfo(getPackageName(), 0).versionCode;
//            int oldVersionCode = mDataStore.getVersion();
//            if (oldVersionCode != 0 && oldVersionCode != newVersionCode) {
//                onVersionUpdate(oldVersionCode, newVersionCode);
//            }
//            mDataStore.persistVersion(newVersionCode);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.socialone.android",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public static Context getContext() {
        return mContext;
    }

    private void onVersionUpdate(int oldVersionCode, int newVersionCode) {
        //this method is called when the version code changes, use comparison operators to control migration
    }
}

