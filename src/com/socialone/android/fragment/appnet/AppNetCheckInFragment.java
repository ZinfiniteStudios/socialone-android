package com.socialone.android.fragment.appnet;

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
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.plus.PlusShare;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.appnet.adnlib.Annotations;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.PlaceSearchQueryParameters;
import com.socialone.android.appnet.adnlib.data.Annotation;
import com.socialone.android.appnet.adnlib.data.Place;
import com.socialone.android.appnet.adnlib.data.PlaceList;
import com.socialone.android.appnet.adnlib.data.Post;
import com.socialone.android.appnet.adnlib.response.PlaceListResponseHandler;
import com.socialone.android.appnet.adnlib.response.PostResponseHandler;
import com.socialone.android.services.LocationService;
import com.socialone.android.utils.Constants;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.ArrayList;
import java.util.HashMap;

import oauth.signpost.OAuth;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by david.hodge on 12/30/13.
 */
public class AppNetCheckInFragment extends SherlockFragment {

    View view;
    ListView listView;
    AppDotNetClient client;
    LocationManager locationManager;
    Location location;
    String lat;
    String lon;
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
        getUserLocation();
    }

    private void getUserLocation() {
        try {
            location = locationService.getLocation();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
            client = new AppDotNetClient(prefs.getString(OAuth.OAUTH_TOKEN, null));
            PlaceSearchQueryParameters placeSearchQueryParameters = new PlaceSearchQueryParameters(location.getLatitude(), location.getLongitude());
            client.retrievePlacesWithSearchQuery(placeSearchQueryParameters, new PlaceListResponseHandler() {
                @Override
                public void onSuccess(PlaceList responseData) {
                    final ArrayList<Place> places = responseData;
                    getSherlockActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                            googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), places);
                            SwingBottomInAnimationAdapter swingBottomInAnimationAdapter =  new SwingBottomInAnimationAdapter(googleCardsAdapter);
                            swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                            swingBottomInAnimationAdapter.setAbsListView(listView);
                            listView.setAdapter(swingBottomInAnimationAdapter);
                            googleCardsAdapter.setData(places);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void checkinDialog(final Place place){
        final String placeName = place.getName();
        final String placeId = place.getFactualId();
        final String placeAdd = place.getAddress();

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
                HashMap<String, Object> list2 = new HashMap<String, Object>();
                //TODO fill out all info as needed here
                list2.put("id", placeId);
                list2.put("address", placeAdd);
                list2.put("name", placeName);

                Post post = new Post();
                Annotation annotation = new Annotation();
                annotation.setType(Annotations.OHAI_LOCATION);
                annotation.setValue(list2);

                post.setText(checkinMessge.getText().toString());
                post.addAnnotation(annotation);
                client.createPost(post, new PostResponseHandler() {
                    @Override
                    public void onSuccess(Post responseData) {
                        dialog.dismiss();
                        if (gplusCheckbox.isChecked()) {
                            Uri locUrl;
                            try {
                                locUrl = Uri.parse(place.getWebsite());
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
                    public void onError(Exception error) {
                        super.onError(error);
                        Log.d("checkin", error.getLocalizedMessage());
                    }
                });

                if(twitterCheckbox.isChecked()){
                    try {
                        StatusUpdate statusUpdate = new StatusUpdate("I'm at " + place.getName() + " via @SocialOne_App");
                        statusUpdate.setPlaceId(place.getFactualId());
                        GeoLocation geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
                        statusUpdate.setLocation(geoLocation);
                        statusUpdate.setDisplayCoordinates(true);
                        statusUpdate.placeId(place.getFactualId());
                        Status status = twitter.updateStatus(statusUpdate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            };
        });

        SpannableString str = new SpannableString(placeName);
        str.setSpan(new ForegroundColorSpan(Color.BLACK), 0, placeName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        dialog.setTitle(str);
        dialog.show();
    }


    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Place> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, ArrayList<Place> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(ArrayList<Place> appPlace){
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
        public Place getItem(int position) {
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

            final Place place = getItem(position);
            viewHolder.textView.setText(place.getName());
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkinDialog(place);
                }
            });

            viewHolder.locAddr.setText(place.getAddress());
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
