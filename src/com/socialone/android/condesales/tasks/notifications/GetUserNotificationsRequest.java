package com.socialone.android.condesales.tasks.notifications;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.socialone.android.condesales.constants.FoursquareConstants;
import com.socialone.android.condesales.listeners.GetNotificationsListener;
import com.socialone.android.condesales.models.Notifications;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by david.hodge on 1/6/14.
 */
public class GetUserNotificationsRequest extends AsyncTask<String, Integer, ArrayList<Notifications>> {

    private Activity mActivity;
    private ProgressDialog mProgress;
    private GetNotificationsListener mListener;
    private Exception error;

    public GetUserNotificationsRequest(Activity activity, GetNotificationsListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    public GetUserNotificationsRequest(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        mProgress = new ProgressDialog(mActivity);
        mProgress.setCancelable(false);
        mProgress.setMessage("Getting user info ...");
        mProgress.show();
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Notifications> doInBackground(String... params) {

        String access_token = params[0];
        Notifications venue = null;
        ArrayList<Notifications> list = new ArrayList<Notifications>();
        try {
            // date required
            String apiDateVersion = FoursquareConstants.API_DATE_VERSION;
            // Call Foursquare to post checkin
            JSONObject venuesJson = executeHttpGet("https://api.foursquare.com/v2/updates/notifications"
                    + "?v="
                    + apiDateVersion
                    + "&oauth_token=" + access_token);

            // Get return code
            int returnCode = Integer.parseInt(venuesJson.getJSONObject("meta")
                    .getString("code"));
            // 200 = OK
            if (returnCode == 200) {
                Log.d("foursquare", venuesJson.toString());
                Gson gson = new Gson();
                Log.d("foursquare", venuesJson.getJSONObject("response").toString());
                Log.d("foursquare", venuesJson.getJSONObject("response").getJSONObject("notifications").toString());
                Log.d("foursquare", "items " + venuesJson.getJSONObject("response").getJSONObject("notifications").getJSONArray("items").toString());
                Log.d("foursquare", venuesJson.getJSONObject("response").getJSONObject("notifications").getString("count"));
                JSONArray json = venuesJson.getJSONObject("response")
                        .getJSONObject("notifications").getJSONArray("items");
                Log.d("foursquare", "json " + json.toString());
                Log.d("foursquare", "json length " + json.length());
                for (int i = 0; i < json.length(); i++) {
                    Log.d("foursquare", json.get(i).toString());
                    venue = gson.fromJson(json.get(i).toString(), Notifications.class);
                    list.add(venue);
                }
            } else {
                if (mListener != null)
                    mListener.onError(venuesJson.getJSONObject("meta")
                            .getString("errorDetail"));
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            if (mListener != null)
                mListener.onError(exp.toString());
        }
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<Notifications> result) {
        mProgress.dismiss();
        if (mListener != null)
            mListener.onGotNotifications(result);
        super.onPostExecute(result);
    }

    /**
     * Calls a URI and returns the answer as a JSON object.
     *
     * @param uri
     *            the uri to make the request
     * @return The JSONObject containing the information
     * @throws Exception
     *             general exception
     */
    private JSONObject executeHttpGet(String uri) throws Exception {
        HttpGet req = new HttpGet(uri);

        HttpClient client = new DefaultHttpClient();
        HttpResponse resLogin = client.execute(req);
        BufferedReader r = new BufferedReader(new InputStreamReader(resLogin
                .getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = r.readLine()) != null) {
            sb.append(s);
        }

        return new JSONObject(sb.toString());
    }
}