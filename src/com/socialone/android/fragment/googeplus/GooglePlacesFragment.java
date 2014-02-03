package com.socialone.android.fragment.googeplus;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.googleplaces.GooglePlaces;
import com.socialone.android.googleplaces.models.Place;
import com.socialone.android.googleplaces.models.PlacesResult;
import com.socialone.android.googleplaces.models.Result;
import com.socialone.android.places.Response;
import com.socialone.android.places.callback.PlaceCallback;
import com.socialone.android.places.request.PlaceParams;
import com.socialone.android.places.request.PlaceSearch;
import com.socialone.android.places.request.PlacesClient;
import com.socialone.android.utils.Constants;

import java.util.List;

/**
 * Created by david.hodge on 1/29/14.
 */
public class GooglePlacesFragment extends SherlockFragment {
    View view;
    ListView listView;
    PlaceCallback callback;
    PlaceParams params;
    Location location;
    LocationManager locationManager;
    GooglePlaces googlePlaces;
    GoogleCardsAdapter googleCardsAdapter;
    PlusClient plusClient;
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private ConnectionResult mConnectionResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        plusClient = new PlusClient.Builder(getSherlockActivity(), new GooglePlayServicesClient.ConnectionCallbacks() {
//            @Override
//            public void onConnected(Bundle bundle) {
//                Toast.makeText(getSherlockActivity(), "Welcome from G+ with results " + plusClient.getCurrentPerson().getCurrentLocation(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onDisconnected() {
//                Log.e("google_place", "g+ login error");
//            }
//        }, new GooglePlayServicesClient.OnConnectionFailedListener() {
//            @Override
//            public void onConnectionFailed(ConnectionResult connectionResult) {
//
//            }
//        }).setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/CheckInActivity")
////                .setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/CheckInActivity")
//                .setScopes(Scopes.PLUS_LOGIN, Scopes.PLUS_PROFILE, Scopes.APP_STATE).build();
//
//        plusClient.connect();

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

        googlePlaces = new GooglePlaces(Constants.GOOGLE_KEY);
        locationManager = (LocationManager) getSherlockActivity().getSystemService(getSherlockActivity().LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
        location = locationManager.getLastKnownLocation(bestProvider);

//        PlacesSettings.getInstance().setApiKey(Constants.GOOGLE_KEY);

        params = new PlaceSearch().nearbySearch(location.getLatitude(), location.getLongitude(), 1500);
        params.setKeyword("food");
        callback = new PlaceCallback() {
            @Override
            public void onSuccess(Response response) {
                Log.d("google_place", response.toString());
            }

            @Override
            public void onException(Exception exception) {
                Log.e("google_place", exception.toString());
            }
        };
        PlacesClient.sendRequest(params, callback);
        try {
            PlacesResult result = googlePlaces.getPlaces("food", 500, location.getLatitude(), location.getLongitude());
            if (result.getStatusCode() == Result.StatusCode.OK) {
                List<Place> places = result.getPlaces();
                googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), places);
                SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
                swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                swingBottomInAnimationAdapter.setAbsListView(listView);
                listView.setAdapter(swingBottomInAnimationAdapter);
                googleCardsAdapter.setData(places);
                Log.e("google_place", "response " + places.toString());
            } else {
                Log.e("google_place", "response error " + result.toString());
            }
        } catch (Exception e) {
            Log.e("google_place", e.toString());
        }
    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<Place> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, List<Place> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(List<Place> appPlace) {
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

                    Intent shareIntent = new PlusShare.Builder(mContext)
                            .setType("text/plain")
                            .setText(place.getName())
                            .setContentUrl(Uri.parse("http://maps.google.com/maps?q=" + place.getLatitude() + "," + place.getLongitude()))
                            .getIntent();
                    startActivityForResult(shareIntent, 0);

                }
            });


            return view;
        }

        public class ViewHolder {
            TextView textView;
            Button checkBox;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        plusClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
//        plusClient.disconnect();
    }
}
