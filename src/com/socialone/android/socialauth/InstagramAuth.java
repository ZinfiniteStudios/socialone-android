package com.socialone.android.socialauth;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.socialone.android.utils.Constants;
import com.socialone.android.utils.Datastore;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import roboguice.RoboGuice;

/**
 * Created by david.hodge on 12/24/13.
 */
public class InstagramAuth extends LoginAdapter {

    SocialAuthAdapter mAuthAdapter;
    Datastore mDatastore;

    public InstagramAuth(final Context context) {
        super(context);
        mDatastore = RoboGuice.getInjector(context).getInstance(Datastore.class);
        mAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(final Bundle bundle) {
//                mDatastore.setTwitterAccessKey(
//                        mAuthAdapter.getCurrentProvider().getAccessGrant().getKey());
//                mDatastore.setTwitterAccessSecret(
//                        mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
                if (mListener != null){
                    Handler uiHandler = new Handler(context.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onComplete(bundle);
                        }
                    });
                }
            }

            @Override
            public void onError(final SocialAuthError socialAuthError) {
                if (mListener != null) {
                    Handler uiHandler = new Handler(context.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            if (socialAuthError.getInnerException() instanceof UserDeniedPermissionException) {
//                                mListener.onCancel();
//                            } else {
//                                mListener.onError(socialAuthError);
//                            }
                        }
                    });
                }
            }

            @Override
            public void onCancel() {
                if (mListener != null) {
                    Handler uiHandler = new Handler(context.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onCancel();
                        }
                    });
                }
            }

            @Override
            public void onBack() {
                if (mListener != null) {
                    Handler uiHandler = new Handler(context.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onBack();
                        }
                    });
                }
            }
        });
        mAuthAdapter.addCallBack(SocialAuthAdapter.Provider.INSTAGRAM, Constants.INSTAGRAM_CALLBACK);
    }

    @Override
    public void authorize() {
        mAuthAdapter.authorize(mContext, SocialAuthAdapter.Provider.INSTAGRAM);
    }

    @Override
    public void signOut() {
        mAuthAdapter.signOut(mContext, SocialAuthAdapter.Provider.INSTAGRAM.toString());
//        mDatastore.clearTwitterToken();
    }

    @Override
    public boolean isSignedIn() {
        return mDatastore.getUserLoggedInToTwitter();
    }
}
