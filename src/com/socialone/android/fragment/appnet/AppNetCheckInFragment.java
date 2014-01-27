package com.socialone.android.fragment.appnet;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.crashlytics.android.Crashlytics;
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

import java.util.ArrayList;
import java.util.HashMap;

import oauth.signpost.OAuth;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    public void checkinDialog(Place place){
        final String placeName = place.getName();
        final String placeId = place.getFactualId();
        final String placeAdd = place.getAddress();

        dialog = new Dialog(getSherlockActivity());
        dialog.setContentView(R.layout.checkin_dialog);
        checkinMessge =  (EditText) dialog.findViewById(R.id.checkin_message);
        cancelCheckinBtn = (Button) dialog.findViewById(R.id.checkin_message_cancel);
        checkinBtn = (Button) dialog.findViewById(R.id.checkin_message_checkin);

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
                    }

                    @Override
                    public void onError(Exception error) {
                        super.onError(error);
                        Log.d("checkin", error.getLocalizedMessage());
                    }
                });

            };
        });

        dialog.setTitle(placeName);
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
//            setImageView(viewHolder, position);

            return view;
        }

        public class ViewHolder {
            TextView textView;
            Button checkBox;
        }
    }
}
