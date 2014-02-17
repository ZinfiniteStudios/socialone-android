package com.socialone.android.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androauth.api.TumblrApi;
import com.androauth.oauth.OAuth10Service;
import com.androauth.oauth.OAuth10Token;
import com.androauth.oauth.OAuthService;
import com.codepath.oauth.OAuthLoginActivity;
import com.socialone.android.R;
import com.socialone.android.tools.TumblrClient;
import com.socialone.android.utils.Constants;
import com.twotoasters.android.hoot.HootResult;

/**
 * Created by david.hodge on 1/7/14.
 */
public class TumblrAuthActivity extends OAuthLoginActivity<TumblrClient> {
    OAuth10Service service;

    public final static String PARAMETER_CONSUMER_KEY = Constants.TUMBLR_CONSUMER_KEY;
    public final static String PARAMETER_CONSUMER_SECRET = Constants.TUMBLR_CONSUMER_SECRET;
    public final static String PARAMETER_CALLBACK_URL = Constants.TUMBLR_CALLBACK;

    private String consumerKey;
    private String consumerSecret;
    private String callbackUrl;
//    private OnLoginHandler loginHandler;
//
//    public interface OnLoginHandler {
//        public void onLogin();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_net_auth);

    /*
     * Get params
     */
//        Intent intent = getIntent();
        consumerKey = PARAMETER_CONSUMER_KEY;
        consumerSecret = PARAMETER_CONSUMER_SECRET;
        callbackUrl = PARAMETER_CALLBACK_URL;

        service = OAuthService.newInstance(new TumblrApi(), consumerKey, consumerSecret, new OAuth10Service.OAuth10ServiceCallback() {

            @Override
            public void onOAuthAccessTokenReceived(OAuth10Token token) {
                Log.d("tumblr", token.getAccessToken());
                complete(token);
            }

            @Override
            public void onOAuthRequestTokenReceived() {
                loadWebView();
            }

            @Override
            public void onOAuthRequestTokenFailed(HootResult result) {
                Log.d("tumblr", result.getException().toString());
            }

            @Override
            public void onOAuthAccessTokenFailed(HootResult result) {
                Log.d("tumblr", result.toString());
            }

        });
        service.setApiCallback(callbackUrl);
        service.start();

        getClient().connect();

//        if (TumblrAuthActivity.this instanceof OnLoginHandler) {
//            loginHandler = (OnLoginHandler) TumblrAuthActivity.this;
//        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView() {
        final WebView webview = (WebView) findViewById(R.id.app_net_auth_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                System.out.println("tumblr callback url " + url);

                // Checking for our successful callback
                if(url.contains(callbackUrl)) {
                    webview.setVisibility(View.GONE);
                    service.getOAuthAccessToken(url);
                    Log.d("tumblr", "tumblr response has callback");
//                    System.out.println("token url " + url + " " + service.getOAuthAccessToken(url));
                } else {
                    Log.d("tumblr", "token url");
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

        });

        webview.loadUrl(service.getAuthorizeUrl());
    }

    private void complete(OAuth10Token token) {
        Log.d("tumblr", "setting token and things");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TumblrAuthActivity.this);
        final SharedPreferences.Editor edit = prefs.edit();
        edit.putString(Constants.TUMBLR_ACCESS, token.getAccessToken());
        edit.putString(Constants.TUMBLR_SECRET, token.getUserSecret());
        edit.putBoolean("tumblr", true);
        edit.commit();
        this.finish();
    }

    @Override
    public void onLoginSuccess() {
        Log.d("DEBUG", "on login success");
        this.finish();
//        getClient().getUserInfo(new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int code, JSONObject response) {
//                try {
//                    JSONObject jsonUser = response.getJSONObject("response")
//                            .getJSONObject("user");
//                    TumblrUser.setCurrentUser(TumblrUser.fromJson(jsonUser));
////                    if (loginHandler != null) {
////                        loginHandler.onLogin();
////                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                Log.d("DEBUG", response.toString());
//            }
//
//            @Override
//            public void onFailure(Throwable error) {
//                Log.d("DEBUG", error.toString());
//            }
//        });
    }

    @Override
    public void onLoginFailure(Exception e) {
        Log.d("DEBUG", e.toString());
    }
}