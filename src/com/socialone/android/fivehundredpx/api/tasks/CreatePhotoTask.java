package com.socialone.android.fivehundredpx.api.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.socialone.android.MainApp;
import com.socialone.android.R;
import com.socialone.android.fivehundredpx.api.PxApi;
import com.socialone.android.fivehundredpx.api.auth.AccessToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by david.hodge on 1/16/14.
 */
public class CreatePhotoTask extends AsyncTask<Object, Void, JSONObject> {

    private static final String TAG = "CreatePhotoTask";


    public interface Delegate {
        public void success(JSONObject obj);
        public void fail();
    }

    private Delegate _d;


    public CreatePhotoTask(Delegate _d) {
        super();
        this._d = _d;
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        AccessToken accessToken = (AccessToken) params[0];
        String name = (String) params[1];
        String desc = (String) params[2];
        final Context context = MainApp.getContext();

        final ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("name", name));
        postParams.add(new BasicNameValuePair("description", desc));
        // dont post to profile. only to library
        postParams.add(new BasicNameValuePair("privacy", "1"));


        final PxApi api = new PxApi(accessToken,
                context.getString(R.string.px_consumer_key),
                context.getString(R.string.px_consumer_secret));
        JSONObject json = api.post("/photos",postParams);

//        try {
//            Log.w(TAG, json.getJSONObject("photo").getString("id"));
//            Log.w(TAG,json.getString("upload_key"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return json;
    }

    @Override
    protected void onPostExecute(JSONObject result) {

        if(null!=result)
            _d.success(result);
    }
}
