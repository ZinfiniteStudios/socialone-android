package com.socialone.android.condesales.tasks.users;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.socialone.android.condesales.constants.FoursquareConstants;
import com.socialone.android.condesales.criterias.TipsCriteria;
import com.socialone.android.condesales.listeners.FriendsCheckInListener;
import com.socialone.android.condesales.models.Checkin;

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
 * Created by david.hodge on 1/27/14.
 */
public class GetFriendsCheckInsRequest  extends
        AsyncTask<String, Integer,ArrayList<Checkin>> {

    private Activity mActivity;
    private ProgressDialog mProgress;
    private FriendsCheckInListener mListener;
    private TipsCriteria mCriteria;

    public GetFriendsCheckInsRequest(Activity activity,
                             FriendsCheckInListener listener, TipsCriteria criteria) {
        mActivity = activity;
        mListener = listener;
        mCriteria = criteria;
    }

    public GetFriendsCheckInsRequest(Activity activity, TipsCriteria criteria) {
        mActivity = activity;
        mCriteria = criteria;
    }

    @Override
    protected void onPreExecute() {
        mProgress = new ProgressDialog(mActivity);
        mProgress.setCancelable(false);
        mProgress.setMessage("Getting tips nearby ...");
//        mProgress.show();
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Checkin> doInBackground(String... params) {

        String access_token = params[0];
        ArrayList<Checkin> tips = new ArrayList<Checkin>();

        try {

            //date required

            String apiDateVersion = FoursquareConstants.API_DATE_VERSION;
            // Call Foursquare to get the Tips around
            JSONObject tipsJson = executeHttpGet("https://api.foursquare.com/v2/checkins/recent"
                    + "?v="
                    + apiDateVersion
                    + "&ll="
                    + mCriteria.getLocation().getLatitude()
                    + ","
                    + mCriteria.getLocation().getLongitude()
                    + "&oauth_token=" + access_token);

            // Get return code
            int returnCode = Integer.parseInt(tipsJson.getJSONObject("meta")
                    .getString("code"));
            // 200 = OK
            if (returnCode == 200) {
                Gson gson = new Gson();
                android.util.Log.d("foursquare", "friends checkins - " + tipsJson.toString());
                JSONArray json = tipsJson.getJSONObject("response")
                        .getJSONArray("recent");
                for (int i = 0; i < json.length(); i++) {
                    Checkin tip = gson.fromJson(json.getJSONObject(i)
                            .toString(), Checkin.class);
                    tips.add(tip);
                }
            } else {
                if (mListener != null)
                    mListener.onError(tipsJson.getJSONObject("meta")
                            .getString("errorDetail"));
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            if (mListener != null)
                mListener.onError(exp.toString());
        }
        return tips;
    }

    @Override
    protected void onPostExecute(ArrayList<Checkin> tips) {
        mProgress.dismiss();
        if (mListener != null)
            mListener.onGotCheckIns(tips);
        super.onPostExecute(tips);
    }

    // Calls a URI and returns the answer as a JSON object
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
