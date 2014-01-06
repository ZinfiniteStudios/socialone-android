package com.socialone.android.condesales.listeners;

import com.socialone.android.condesales.models.Notifications;

import java.util.ArrayList;

/**
 * Created by david.hodge on 1/6/14.
 */
public interface GetNotificationsListener extends ErrorListener{

    public void onGotNotifications(ArrayList<Notifications> list);

}
