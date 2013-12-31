package com.socialone.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.Session;
import com.socialone.android.fragment.SocialConnectFragment;

import java.util.Collection;

/**
 * Created by david.hodge on 12/26/13.
 */
public class Utils {

    public boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    public static Session ensureFacebookSession(Activity sherlockActivity) {
        Session activeSession = Session.getActiveSession();
        if (activeSession == null || !activeSession.getState().isOpened()) {
            activeSession = Session.openActiveSession(sherlockActivity, false, null);
        }

        if (activeSession == null || !activeSession.getState().isOpened()) {
            Intent facebookLoginActivity = new Intent(sherlockActivity, SocialConnectFragment.class);
            sherlockActivity.startActivity(facebookLoginActivity);
        }

        return activeSession;
    }

    public static Session ensureFacebookSessionFromCache(Context context){
        Session activeSession = Session.getActiveSession();
        if (activeSession == null || !activeSession.getState().isOpened()) {
            activeSession = Session.openActiveSessionFromCache(context);
        }
        return activeSession;
    }

}
