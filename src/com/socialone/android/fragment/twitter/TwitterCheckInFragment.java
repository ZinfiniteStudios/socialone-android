package com.socialone.android.fragment.twitter;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.crashlytics.android.Crashlytics;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.utils.Constants;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by david.hodge on 1/30/14.
 */
public class TwitterCheckInFragment extends SherlockFragment {

    View view;
    ListView listView;
    GoogleCardsAdapter googleCardsAdapter;
    SocialAuthAdapter mAuthAdapter;
    Location location;
    LocationManager locationManager;
    String lat;
    String lon;
    TwitterFactory tf;
    Twitter twitter;
    GeoLocation geoLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.social_checkin_list, container, false);
        listView = (ListView) view.findViewById(R.id.activity_googlecards_listview);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserLocation();
    }

    private void getUserLocation() {
        try {
            locationManager = (LocationManager) getSherlockActivity().getSystemService(getSherlockActivity().LOCATION_SERVICE);
            String bestProvider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(bestProvider);
            lat = Double.toString(location.getLatitude());
            lon = Double.toString(location.getLongitude());
            twitterSetup();
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
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
            tf = new TwitterFactory(cb.build());
            twitter = tf.getInstance();
            geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
            GeoQuery query = new GeoQuery(geoLocation);
            query.setAccuracy("10000");
            ResponseList<twitter4j.Place> places = twitter.searchPlaces(query);


            googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), places);
            SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
            swingBottomInAnimationAdapter.setInitialDelayMillis(300);
            swingBottomInAnimationAdapter.setAbsListView(listView);
            listView.setAdapter(swingBottomInAnimationAdapter);
            googleCardsAdapter.setData(places);
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

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ResponseList<twitter4j.Place> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, ResponseList<twitter4j.Place> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(ResponseList<twitter4j.Place> appPlace) {
            mAppPlace = appPlace;
        }

        @Override
        public boolean isEmpty() {
            return mShouldReturnEmpty && super.isEmpty();
        }

        @Override
        public int getCount() {
            return mAppPlace.size();
        }

        @Override
        public twitter4j.Place getItem(int position) {
            return mAppPlace.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.social_checkin_list_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.social_checkin_name);
                viewHolder.checkBox = (Button) view.findViewById(R.id.social_checkin_checkbox);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final twitter4j.Place place = getItem(position);
            viewHolder.textView.setText(place.getName());

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    try {
                        StatusUpdate statusUpdate = new StatusUpdate("I'm at " + place.getName() + " using SocialOne for Android!");
                        statusUpdate.setPlaceId(place.getId());
                        statusUpdate.setLocation(geoLocation);
                        statusUpdate.setDisplayCoordinates(true);
                        statusUpdate.placeId(place.getId());
                        Status status = twitter.updateStatus(statusUpdate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });


            return view;
        }

        public class ViewHolder {
            TextView textView;
            Button checkBox;
        }
    }
}
