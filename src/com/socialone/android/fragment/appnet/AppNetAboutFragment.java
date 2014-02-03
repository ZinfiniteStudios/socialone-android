package com.socialone.android.fragment.appnet;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.socialone.android.R;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.data.User;
import com.socialone.android.appnet.adnlib.response.UserResponseHandler;

import java.util.List;

import oauth.signpost.OAuth;

/**
 * Created by david.hodge on 2/2/14.
 */
public class AppNetAboutFragment extends SherlockFragment {

    View view;
    MapView mapView;
    Bundle bundle;
    GoogleMap mMap;
    TextView twitterUserAbout;
    TextView twitterUserTweets;
    TextView twitterUserFollowing;
    TextView twitterUserFollowers;
    TextView twitterUserFavs;
    TextView twitteruserListed;
    TextView twitterUserLocation;
    AppDotNetClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.twitter_about_fragment, container, false);
        twitterUserAbout = (TextView) view.findViewById(R.id.twitter_user_desc);
        twitterUserTweets = (TextView) view.findViewById(R.id.twitter_user_tweet_count);
        twitterUserFollowing = (TextView) view.findViewById(R.id.twitter_user_following);
        twitterUserFollowers = (TextView) view.findViewById(R.id.twitter_user_followers);
        twitterUserFavs = (TextView) view.findViewById(R.id.twitter_user_favorites);
        twitteruserListed = (TextView) view.findViewById(R.id.twitter_user_listed);
        twitterUserLocation = (TextView) view.findViewById(R.id.twitter_user_location);

        mapView = (MapView) view.findViewById(R.id.checkin_map_fragment);
        mapView.onCreate(bundle);
        setUpMapIfNeeded(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAppNetInfo();
    }

    private void getAppNetInfo(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        client = new AppDotNetClient(prefs.getString(OAuth.OAUTH_TOKEN, null));
        client.retrieveCurrentUser(new UserResponseHandler() {
            @Override
            public void onSuccess(final User responseData) {
                getSherlockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        twitterUserAbout.setText(responseData.getDescription().getText());
                        twitterUserTweets.setText("Post - " + responseData.getCounts().getPosts());
                        twitterUserFollowing.setText("Following - " + responseData.getCounts().getFollowing());
                        twitterUserFollowers.setText("Followers - " + responseData.getCounts().getFollowers());
                        twitterUserFavs.setText("Stars - " + responseData.getCounts().getStars());
                        //TODO remove
                        twitteruserListed.setText("Listed - " + "N/A");
                        twitterUserLocation.setText(responseData.getLocale());
                        getLatitudeAndLongitudeFromGoogleMapForAddress(responseData.getLocale());
                    }
                });

            }

        });
    }

    private void setUpMap() {
        mMap.setIndoorEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(false);
    }

    private void setUpMapIfNeeded(View view) {
        if (mMap == null) {
            mMap = ((MapView) view.findViewById(R.id.checkin_map_fragment)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    public boolean getLatitudeAndLongitudeFromGoogleMapForAddress(String searchedAddress) {
        Geocoder coder = new Geocoder(getSherlockActivity());
        List<Address> address;
        try {
            address = coder.getFromLocationName(searchedAddress, 5);
            if (address == null) {
                Log.d("twitter", "############Address not correct #########");
            }
            Address location = address.get(0);
            final CameraPosition HOME =
                    new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(17)
                            .bearing(-100)
                            .tilt(25)
                            .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(HOME));

            Log.d("twitter", "Address Latitude : " + location.getLatitude() + "Address Longitude : " + location.getLongitude());
            return true;

        } catch (Exception e) {
            Log.d("twitter", "MY_ERROR : ############Address Not Found");
            return false;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
