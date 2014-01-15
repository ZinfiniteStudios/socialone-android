package com.socialone.android.fragment.foursquare;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
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

import org.brickred.socialauth.android.SocialAuthAdapter;

import java.util.ArrayList;
import java.util.Iterator;

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
//        fourSetup();
        getUserLocation();
    }

    private void getUserLocation() {
            locationManager = (LocationManager) getSherlockActivity().getSystemService(getSherlockActivity().LOCATION_SERVICE);
            String bestProvider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(bestProvider);
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
                                    googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), venueArrayList);
                                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter =  new SwingBottomInAnimationAdapter(googleCardsAdapter);
                                    swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                                    swingBottomInAnimationAdapter.setAbsListView(listView);
                                    listView.setAdapter(swingBottomInAnimationAdapter);
                                    googleCardsAdapter.setData(venueArrayList);
                                }
                            });
                            Log.d("places", "response " + venues.toString());
                            Iterator<Venue> itr = venues.listIterator();
                            int z=0,x=0,increment=0;
                            while (itr.hasNext()){
                                String data = itr.next().getName();
                                Log.d("places",z + " " + data);
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

    public void checkinDialog(Venue place){
        final String placeName = place.getName();
        final String placeId = place.getId();

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
                CheckInCriteria checkInCriteria = new CheckInCriteria();
                checkInCriteria.setShout(checkinMessge.getText().toString());
                checkInCriteria.setVenueId(placeId);
                easyFoursquareAsync.checkIn(new CheckInListener() {
                    @Override
                    public void onCheckInDone(Checkin checkin) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(String errorMsg) {
                        dialog.dismiss();

                    }
                }, checkInCriteria);

            }
        });

        dialog.setTitle(placeName);
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

        public void setData(ArrayList<Venue> appPlace){
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
//            setImageView(viewHolder, position);

            return view;
        }

        public class ViewHolder {
            TextView textView;
            Button checkBox;
        }
    }
}
