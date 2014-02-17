package com.socialone.android.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.socialone.android.MainApp;
import com.socialone.android.utils.Constants;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TumblrApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by david.hodge on 2/4/14.
 */
public class TumblrClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TumblrApi.class;
    public static final String REST_URL = "http://api.tumblr.com/v2";
    public static final String REST_CONSUMER_KEY = Constants.TUMBLR_CONSUMER_KEY;
    public static final String REST_CONSUMER_SECRET = Constants.TUMBLR_CONSUMER_SECRET;
    public static final String REST_CALLBACK_URL = Constants.TUMBLR_CALLBACK;

    public TumblrClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getTaggedPhotos(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("tag", "tumblrsnap");
        params.put("limit", "20");
        params.put("api_key", REST_CONSUMER_KEY);
        client.get(getApiUrl("tagged"), params, handler);
    }

    public void getUserPhotos(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("type", "photo");
        params.put("limit", "20");
        params.put("api_key", REST_CONSUMER_KEY);
        client.get(getApiUrl("user/dashboard"), params, handler);
    }

    public void getUserInfo(AsyncHttpResponseHandler handler) {
        client.get(getApiUrl("user/info"), null, handler);
    }

    public void createPhotoPost(String blog, File file, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("type", "photo");
        params.put("tags", "tumblrsnap");
        try {
            params.put("data", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.post(getApiUrl(String.format("blog/%s/post?type=photo&tags=tumblrsnap", blog)), params, handler);
    }

    public void createPhotoPost(String blog, Bitmap bitmap, final AsyncHttpResponseHandler handler) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainApp.getContext());
        RequestParams params = new RequestParams();
        params.put("type", "photo");
        params.put("tags", "tumblrsnap");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] bytes = stream.toByteArray();
        params.put("data", new ByteArrayInputStream(bytes), "image.png");

        client.post(getApiUrl(String.format("blog/%s/post?type=photo", blog)), params, handler);
    }
}
