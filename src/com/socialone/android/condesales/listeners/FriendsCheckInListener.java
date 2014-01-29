package com.socialone.android.condesales.listeners;

import com.socialone.android.condesales.models.Checkin;

import java.util.ArrayList;

/**
 * Created by david.hodge on 1/27/14.
 */
public interface FriendsCheckInListener extends ErrorListener {

    public void onGotCheckIns(ArrayList<Checkin> list);
}
