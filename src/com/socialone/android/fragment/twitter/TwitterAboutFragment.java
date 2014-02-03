package com.socialone.android.fragment.twitter;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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
import com.socialone.android.utils.Constants;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by david.hodge on 2/2/14.
 */
public class TwitterAboutFragment extends SherlockFragment {

    View view;
    SocialAuthAdapter mAuthAdapter;
    TextView twitterUserAbout;
    TextView twitterUserTweets;
    TextView twitterUserFollowing;
    TextView twitterUserFollowers;
    TextView twitterUserFavs;
    TextView twitteruserListed;
    TextView twitterUserLocation;
    MapView mapView;
    Bundle bundle;
    GoogleMap mMap;

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
        twitterSetup();
    }

    private void setUpMap() {
        mMap.setIndoorEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(false);
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(HOME));
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

    private void setUpTwit4j() {
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(Constants.TWIT_CONSUMER_KEY)
                    .setOAuthConsumerSecret(Constants.TWIT_CONSUMER_SECRET)
                    .setOAuthAccessToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey())
                    .setOAuthAccessTokenSecret(mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            User user = twitter.showUser(twitter.getId());

            twitterUserAbout.setText(user.getDescription());
            twitterUserTweets.setText("Tweets - " + user.getStatusesCount());
            twitterUserFollowing.setText("Following - " + user.getFriendsCount());
            twitterUserFollowers.setText("Followers - " + user.getFollowersCount());
            twitterUserFavs.setText("Favs - " + user.getFavouritesCount());
            twitteruserListed.setText("Listed - " + user.getListedCount());

            twitterUserLocation.setText(user.getLocation());
            getLatitudeAndLongitudeFromGoogleMapForAddress(user.getLocation());

            Log.d("twitter user", user.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void twitterSetup() {
        mAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                setUpTwit4j();
            }

            @Override
            public void onError(SocialAuthError socialAuthError) {
                Log.e("twitter", "auth adapter " + socialAuthError.getMessage());
            }

            @Override
            public void onCancel() {
                //stub
            }

            @Override
            public void onBack() {
                //stub
            }
        });
        mAuthAdapter.addCallBack(SocialAuthAdapter.Provider.TWITTER, Constants.TWITTER_CALLBACK);
        mAuthAdapter.authorize(getSherlockActivity(), SocialAuthAdapter.Provider.TWITTER);
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
