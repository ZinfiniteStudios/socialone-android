package com.socialone.android.fragment.facebook;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphPlace;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.plus.PlusShare;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.services.LocationService;
import com.socialone.android.utils.Constants;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Created by david.hodge on 12/25/13.
 */
public class FacebookCheckInFragment extends SherlockFragment implements LocationListener{

    View view;
    ListView listView;
    private UiLifecycleHelper uiHelper;
    Session session;
    Location location;
    LocationManager locationManager;
    GoogleCardsAdapter googleCardsAdapter;

    Dialog dialog;
    EditText checkinMessge;
    Button cancelCheckinBtn;
    Button checkinBtn;

    LocationService locationService;

    TwitterFactory tf;
    Twitter twitter;
    SocialAuthAdapter mAuthAdapter;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        edit = prefs.edit();
        locationService = new LocationService(getSherlockActivity());

        uiHelper = new UiLifecycleHelper(getSherlockActivity(), new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        });
        uiHelper.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.social_checkin_list, container, false);
        listView = (ListView) view.findViewById(R.id.activity_googlecards_listview);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLocations();
    }

    private void getLocations(){
//        locationManager = (LocationManager) getSherlockActivity().getSystemService(getSherlockActivity().LOCATION_SERVICE);
//        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
//        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        location = locationService.getLocation();
        getFacebookLocations(location);
//        if(location != null) {
//            Log.v("location", location.getLatitude() + " and " + location.getLongitude());
//            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            getFacebookLocations(location);
//        }else {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        }
    }

    public void getFacebookLocations(Location location){
        session = ensureFacebookSessionFromCache(getSherlockActivity());
//        Request locRequest = new Request.newPlacesSearchRequest(session, location, 100, 25, null, new Request.GraphPlaceListCallback())
        Bundle params = new Bundle();
        params.putString("type", "place");
        params.putString("center", location.getLatitude() + "," + location.getLongitude());
        params.putString("distance", "5000");
        params.putString("limit", "40");

        Request.GraphPlaceListCallback graphPlaceListCallback = new Request.GraphPlaceListCallback() {
            @Override
            public void onCompleted(List<GraphPlace> places, Response response) {
                googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), places);
                SwingBottomInAnimationAdapter swingBottomInAnimationAdapter =  new SwingBottomInAnimationAdapter(googleCardsAdapter);
                swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                swingBottomInAnimationAdapter.setAbsListView(listView);
                listView.setAdapter(swingBottomInAnimationAdapter);
                googleCardsAdapter.setData(places);
            }
        };
        Request request = new Request().newPlacesSearchRequest(session, location, 500, 40, null, graphPlaceListCallback);

        Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                Log.d("places", response.toString());
            }
        };

        request.executeAsync();
        Request request1 = new Request(session, "/search", params,
                HttpMethod.GET, callback);
        request1.executeAsync();
    }

    public void onLocationChanged(Location location) {
        Log.v("location", location.getLatitude() + " and " + location.getLongitude());
        getFacebookLocations(location);
        if (location != null) {
            Log.v("location", location.getLatitude() + " and " + location.getLongitude());
            locationManager.removeUpdates(this);
            getFacebookLocations(location);
        }
    }

    public void onProviderDisabled(String arg0) {}
    public void onProviderEnabled(String arg0) {}
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
//            Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
//            getUserInfo();
        } else if (state.isClosed()) {
            Toast.makeText(getSherlockActivity(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(getSherlockActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(getSherlockActivity());
    }

    public static Session ensureFacebookSessionFromCache(Context context){
        Session activeSession = Session.getActiveSession();
        if (activeSession == null || !activeSession.getState().isOpened()) {
            activeSession = Session.openActiveSessionFromCache(context);
        }
        return activeSession;
    }

    public void checkinDialog(final GraphPlace place){
        final String placeName = place.getName();
        final String placeId = place.getId();

        dialog = new Dialog(getSherlockActivity());
        dialog.setContentView(R.layout.checkin_dialog);
        checkinMessge =  (EditText) dialog.findViewById(R.id.checkin_message);
        cancelCheckinBtn = (Button) dialog.findViewById(R.id.checkin_message_cancel);
        checkinBtn = (Button) dialog.findViewById(R.id.checkin_message_checkin);

        final CheckBox twitterCheckbox = (CheckBox) dialog.findViewById(R.id.twitter_share_box);
        final CheckBox gplusCheckbox = (CheckBox) dialog.findViewById(R.id.gplus_share_box);


        twitterCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    if(prefs.getBoolean("twit_p", false) == true) {
                        mAuthAdapter = new SocialAuthAdapter(new DialogListener() {
                            @Override
                            public void onComplete(Bundle bundle) {
                                ConfigurationBuilder cb = new ConfigurationBuilder();
                                cb.setDebugEnabled(true)
                                        .setOAuthConsumerKey(Constants.TWIT_CONSUMER_KEY)
                                        .setOAuthConsumerSecret(Constants.TWIT_CONSUMER_SECRET)
                                        .setOAuthAccessToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey())
                                        .setOAuthAccessTokenSecret(mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
                                tf = new TwitterFactory(cb.build());
                                twitter = tf.getInstance();
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
                    }else{
                        Toast.makeText(getActivity(), "Twitter unlock must be purchased!", Toast.LENGTH_SHORT).show();
                        twitterCheckbox.setChecked(false);
                    }
                }
            }
        });

        cancelCheckinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //just text post
                Bundle postParams = new Bundle();
                postParams.putString("type", "checkin");
                postParams.putString("message", checkinMessge.getText().toString());
                postParams.putString("place", placeId);
                Request.Callback callback = new Request.Callback() {
                    public void onCompleted(Response response) {
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            dialog.dismiss();
                            Toast.makeText(getSherlockActivity(),
                                    error.getErrorMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getSherlockActivity(),
                                    "Facebook Share completed",
                                    Toast.LENGTH_LONG).show();
                            if (gplusCheckbox.isChecked()) {
                                Uri locUrl;
                                try {
                                    locUrl = Uri.parse(place.getId());
                                } catch (Exception e) {
                                    Log.e("socialone", e.toString());
                                    //TODO change to app site
                                    locUrl = Uri.parse(Constants.APP_RATE_URL);
                                }

                                Intent shareIntent = new PlusShare.Builder(getActivity())
                                        .setType("text/plain")
                                        .setText("At " + place.getName() + " " + checkinMessge.getText().toString())
                                        .setContentUrl(locUrl)
                                        .getIntent();

                                getActivity().startActivityForResult(shareIntent, 0);

                            }
                        }
                    }
                };
                Request request = new Request(session, "me/feed", postParams,
                        HttpMethod.POST, callback);

                RequestAsyncTask task = new RequestAsyncTask(request);
                task.execute();

                if(twitterCheckbox.isChecked()){
                    try {
                        StatusUpdate statusUpdate = new StatusUpdate("I'm at " + place.getName() + " via @SocialOne_App");
                        statusUpdate.setPlaceId(place.getId());
                        GeoLocation geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
                        statusUpdate.setLocation(geoLocation);
                        statusUpdate.setDisplayCoordinates(true);
                        statusUpdate.placeId(place.getId());
                        Status status = twitter.updateStatus(statusUpdate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        SpannableString str = new SpannableString(placeName);
        str.setSpan(new ForegroundColorSpan(Color.BLACK), 0, placeName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        dialog.setTitle(str);
        dialog.show();
    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<GraphPlace> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, List<GraphPlace> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(List<GraphPlace> appPlace){
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
        public GraphPlace getItem(int position) {
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
                viewHolder.locAddr = (TextView) view.findViewById(R.id.social_checkin_address);
                viewHolder.mapbtn = (Button) view.findViewById(R.id.social_checkin_map_btn);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final GraphPlace place = getItem(position);
            viewHolder.textView.setText(place.getName());
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkinDialog(place);
                }
            });

            viewHolder.locAddr.setText(place.getLocation().getStreet());
            viewHolder.mapbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });

            return view;
        }

        public class ViewHolder {
            TextView textView;
            Button checkBox;

            TextView locAddr;
            Button mapbtn;
        }
    }
}
