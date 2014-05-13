package com.socialone.android.fragment.foursquare;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
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
import com.google.android.gms.plus.PlusShare;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.condesales.EasyFoursquareAsync;
import com.socialone.android.condesales.criterias.CheckInCriteria;
import com.socialone.android.condesales.criterias.VenuesCriteria;
import com.socialone.android.condesales.listeners.AccessTokenRequestListener;
import com.socialone.android.condesales.listeners.CheckInListener;
import com.socialone.android.condesales.listeners.FoursquareVenuesResquestListener;
import com.socialone.android.condesales.models.Checkin;
import com.socialone.android.condesales.models.Venue;
import com.socialone.android.services.LocationService;
import com.socialone.android.utils.Constants;
import com.socialone.android.utils.MimicryAdapter;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.ArrayList;
import java.util.Iterator;

import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by david.hodge on 12/25/13.
 */
public class FourSquareCheckInFragment extends SherlockFragment {

    View view;
    ListView listView;
    LocationManager locationManager;
    Location location;
    SocialAuthAdapter linkAuthAdapter;
    EasyFoursquareAsync easyFoursquareAsync;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        locationService = new LocationService(getSherlockActivity());
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
//        fourSetup();
        getUserLocation();
    }

    private void getUserLocation() {
//            locationManager = (LocationManager) getSherlockActivity().getSystemService(getSherlockActivity().LOCATION_SERVICE);
//            String bestProvider = locationManager.getBestProvider(new Criteria(), false);
//            location = locationManager.getLastKnownLocation(bestProvider);
        location = locationService.getLocation();
//            lat = Double.toString(location.getLatitude());
//            lon = Double.toString(location.getLongitude());
        easyFoursquareAsync = new EasyFoursquareAsync(getSherlockActivity());
        easyFoursquareAsync.requestAccess(new AccessTokenRequestListener() {
            @Override
            public void onAccessGrant(String accessToken) {
                VenuesCriteria criteria = new VenuesCriteria();
                criteria.setLocation(location);
                criteria.setQuantity(40);
                easyFoursquareAsync.getVenuesNearby(new FoursquareVenuesResquestListener() {
                    @Override
                    public void onVenuesFetched(ArrayList<Venue> venues) {
                        final ArrayList<Venue> venueArrayList = venues;
                        getSherlockActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DisplayMetrics metrics = new DisplayMetrics();
                                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                                float scaleFactor = metrics.density;
                                int widthPixels = metrics.widthPixels;
                                int heightPixels = metrics.heightPixels;

                                float widthDp = widthPixels / scaleFactor;
                                float heightDp = heightPixels / scaleFactor;

                                float smallestWidth = Math.min(widthDp, heightDp);


                                if (smallestWidth > 720) {
                                    googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), venueArrayList);
                                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
                                    swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                                    swingBottomInAnimationAdapter.setAbsListView(listView);
                                    listView.setAdapter(swingBottomInAnimationAdapter);
                                    final MimicryAdapter adapter = new MimicryAdapter(getSherlockActivity(), 2, swingBottomInAnimationAdapter);
                                    listView.setAdapter(adapter);
                                    googleCardsAdapter.setData(venueArrayList);
                                } else {
                                    googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), venueArrayList);
                                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
                                    swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                                    swingBottomInAnimationAdapter.setAbsListView(listView);
                                    listView.setAdapter(swingBottomInAnimationAdapter);
                                    googleCardsAdapter.setData(venueArrayList);
                                }
                            }
                        });
                        Log.d("places", "response " + venues.toString());
                        Iterator<Venue> itr = venues.listIterator();
                        int z = 0, x = 0, increment = 0;
                        while (itr.hasNext()) {
                            String data = itr.next().getName();
                            Log.d("places", z + " " + data);
                            z++;
                        }
                    }

                    @Override
                    public void onError(String errorMsg) {

                    }
                }, criteria);
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    public void checkinDialog(final Venue place) {
        final String placeName = place.getName();
        final String placeId = place.getId();

        dialog = new Dialog(getSherlockActivity());
        dialog.setContentView(R.layout.checkin_dialog);
        checkinMessge = (EditText) dialog.findViewById(R.id.checkin_message);
        cancelCheckinBtn = (Button) dialog.findViewById(R.id.checkin_message_cancel);
        checkinBtn = (Button) dialog.findViewById(R.id.checkin_message_checkin);

        final CheckBox twitterCheckbox = (CheckBox) dialog.findViewById(R.id.twitter_share_box);
        final CheckBox gplusCheckbox = (CheckBox) dialog.findViewById(R.id.gplus_share_box);

        twitterCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){

                if (prefs.getBoolean("twit_p", false) == true) {
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
                } else {
                    Toast.makeText(getActivity(), "Twitter unlock must be purchased!", Toast.LENGTH_SHORT).show();
                    twitterCheckbox.setChecked(false);
                }
            }
//            }
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
                CheckInCriteria checkInCriteria = new CheckInCriteria();
                checkInCriteria.setLocation(location);
                checkInCriteria.setShout(checkinMessge.getText().toString());
                checkInCriteria.setVenueId(placeId);
                easyFoursquareAsync.checkIn(new CheckInListener() {
                    @Override
                    public void onCheckInDone(Checkin checkin) {
                        dialog.dismiss();
                        if (gplusCheckbox.isChecked()) {
                            Uri locUrl;
                            try {
                                locUrl = Uri.parse(place.getUrl());
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

                    @Override
                    public void onError(String errorMsg) {
                        dialog.dismiss();

                    }
                }, checkInCriteria);

                if (twitterCheckbox.isChecked()) {
                    try {
                        StatusUpdate statusUpdate = new StatusUpdate("I'm at " + place.getName() + " via @SocialOne_App");
                        statusUpdate.setPlaceId(place.getId());
                        GeoLocation geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
                        statusUpdate.setLocation(geoLocation);
                        statusUpdate.setDisplayCoordinates(true);
                        statusUpdate.placeId(place.getId());
                        twitter.updateStatus(statusUpdate);
                    } catch (Exception e) {
                        Log.e("twitter", e.toString());
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
        private ArrayList<Venue> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, ArrayList<Venue> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(ArrayList<Venue> appPlace) {
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
        public Venue getItem(int position) {
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

            final Venue place = getItem(position);
            viewHolder.textView.setText(place.getName());

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkinDialog(place);
                }
            });

            try {
                Log.d("social", place.getLocation().getAddress());
                viewHolder.locAddr.setText(place.getLocation().getAddress());
            } catch (Exception e) {
                if (place.getLocation().getCity() != null) {
                    viewHolder.locAddr.setText(place.getLocation().getCity() + ", " + place.getLocation().getState());
                } else {
                    viewHolder.locAddr.setText(place.getLocation().getState() + ", " + place.getLocation().getCountry());
                }
            }


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
