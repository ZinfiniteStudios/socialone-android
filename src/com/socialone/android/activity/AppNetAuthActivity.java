package com.socialone.android.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.parse.signpost.OAuth;
import com.socialone.android.R;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.data.User;
import com.socialone.android.appnet.adnlib.response.UserResponseHandler;

/**
 * Created by david.hodge on 12/28/13.
 */
public class AppNetAuthActivity extends SherlockActivity {

    String url = "https://account.app.net/oauth/authenticate?client_id="
            + "zza9bRtZ63UGNAxG965a3Kr2uUkmXAqr"
            + "&response_type=token&redirect_uri=http://social.zinfinitestudios.com/v1/callback/&scope=stream,write_post," +
            "follow,messages&new_onboarding=1";

    WebView webView;
    AppDotNetClient appDotNetClient;
    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("App.net Login");


        setContentView(R.layout.app_net_auth);

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        cookieManager.setAcceptCookie(true);

        webView = (WebView) findViewById(R.id.app_net_auth_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("#access_token")) {
                    accessToken = url.replace(
                            "http://social.zinfinitestudios.com/v1/callback/#access_token=", "");

                    appDotNetClient = new AppDotNetClient(accessToken);
                    appDotNetClient.retrieveCurrentUser(new UserResponseHandler() {
                        @Override
                        public void onSuccess(User responseData) {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppNetAuthActivity.this);
                            final SharedPreferences.Editor edit = prefs.edit();
                            edit.putString(OAuth.OAUTH_TOKEN, accessToken);
                            edit.putBoolean("appnet", true);
                            edit.commit();
                            Log.d("app.net", "Welcome " + responseData.getName());
                            finish();
                        }

                        @Override
                        public void onError(Exception error) {
                            super.onError(error);
                            Log.e("app.net", error.getMessage());
                        }
                    });
//                    setContentView(R.layout.loading);
//                    try {
//                        TwitterUser user = new VerifyCredentialsTask().execute(
//                                accessToken).get();
//
//                        TwitterManager.get().setSocialNetType(
//                                SocialNetConstant.Type.Appdotnet,
//                                ConsumerKeyConstants.APPDOTNET_CONSUMER_KEY,
//                                ConsumerKeyConstants.APPDOTNET_CONSUMER_SECRET,
//                                user.getScreenName().toLowerCase() + "_appdotnet");
//
//                        onSuccessfulLogin(user, accessToken);
//                    } catch (Exception e) {
//                        return false;
//                    }
                }
                return false;
            }

        });

        webView.loadUrl(url);
    }

    /*
     *
         */
//    void onSuccessfulLogin(TwitterUser user, String accessToken) {
//        App app = (App) getApplication();
//        app.onPostSignIn(user, accessToken, null,
//                SocialNetConstant.Type.Appdotnet);
//        app.restartApp(this);
//    }
//
//    private class VerifyCredentialsTask extends
//            AsyncTask<String, Void, TwitterUser> {
//        @Override
//        protected TwitterUser doInBackground(String... accessTokens) {
//            if (accessTokens.length == 0) {
//                return null;
//            }
//            return new AppdotnetApi(SocialNetConstant.Type.Appdotnet,
//                    ConsumerKeyConstants.APPDOTNET_CONSUMER_KEY,
//                    ConsumerKeyConstants.APPDOTNET_CONSUMER_SECRET,
//                    null).verifyCredentialsSync(
//                    accessTokens[0], null);
//        }
//    }
}
