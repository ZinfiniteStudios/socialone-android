package com.socialone.android.fragment;

import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.crashlytics.android.Crashlytics;
import com.crittercism.app.Crittercism;
import com.parse.signpost.OAuth;
import com.socialone.android.R;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.PlaceSearchQueryParameters;
import com.socialone.android.appnet.adnlib.data.Place;
import com.socialone.android.appnet.adnlib.data.PlaceList;
import com.socialone.android.appnet.adnlib.response.PlaceListResponseHandler;

import java.util.Iterator;

/**
 * Created by david.hodge on 12/30/13.
 */
public class AppNetCheckInFragment extends SherlockFragment {

    View view;
    AppDotNetClient client;
    LocationManager locationManager;
    Location location;
    String lat;
    String lon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.about_fragment, container, false);
        getUserLocation();
        return view;
    }

    private void getUserLocation() {
        try {
            locationManager = (LocationManager) getSherlockActivity().getSystemService(getSherlockActivity().LOCATION_SERVICE);
            String bestProvider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(bestProvider);
//            lat = Double.toString(location.getLatitude());
//            lon = Double.toString(location.getLongitude());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
            client = new AppDotNetClient(prefs.getString(OAuth.OAUTH_TOKEN, null));
            PlaceSearchQueryParameters placeSearchQueryParameters = new PlaceSearchQueryParameters(location.getLatitude(), location.getLongitude());
            client.retrievePlacesWithSearchQuery(placeSearchQueryParameters, new PlaceListResponseHandler() {
                @Override
                public void onSuccess(PlaceList responseData) {
                    Log.d("places", "response " + responseData.toString());
                    Iterator<Place> itr = responseData.listIterator();
                    int z=0,x=0,increment=0;
                    while (itr.hasNext()){
                        String data = itr.next().getName();
                        Log.d("place",z + " " + data);
                        z++;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Crittercism.logHandledException(e);
            Crashlytics.logException(e);
        }
    }
}
