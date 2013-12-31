package com.socialone.android.socialauth;

import android.content.Context;
/**
 * Created by david.hodge on 11/4/13.
 */
public abstract class LoginAdapter {
    Context mContext;
    LoginListener mListener;

    public LoginAdapter(Context context) {
        mContext = context;
    }

    public LoginAdapter setListener(LoginListener listener) {
        mListener = listener;
        return this;
    }

    public abstract void authorize();

    public abstract void signOut();

    public abstract boolean isSignedIn();
}
