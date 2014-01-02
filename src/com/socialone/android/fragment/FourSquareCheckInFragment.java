package com.socialone.android.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.condesales.EasyFoursquareAsync;
import com.socialone.android.condesales.criterias.VenuesCriteria;
import com.socialone.android.condesales.listeners.AccessTokenRequestListener;
import com.socialone.android.condesales.listeners.FoursquareVenuesResquestListener;
import com.socialone.android.condesales.models.Venue;

import org.brickred.socialauth.android.SocialAuthAdapter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by david.hodge on 12/25/13.
 */
public class FourSquareCheckInFragment extends RoboSherlockFragment {

    View view;
    ListView listView;
    LocationManager locationManager;
    Location location;
    SocialAuthAdapter linkAuthAdapter;
    EasyFoursquareAsync easyFoursquareAsync;
    GoogleCardsAdapter googleCardsAdapter;

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
//        try {
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

//        } catch (Exception e) {
//            Log.e("places", e.toString());
//            Crittercism.logHandledException(e);
//            Crashlytics.logException(e);
//        }
    }


    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Venue> mAppPlace;
        private LruCache<Integer, Bitmap> mMemoryCache;
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
                viewHolder.checkBox = (CheckBox) view.findViewById(R.id.social_checkin_checkbox);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final Venue place = getItem(position);
            viewHolder.textView.setText(place.getName());
//            setImageView(viewHolder, position);

            return view;
        }

//        private void setImageView(ViewHolder viewHolder, int position) {
//            int imageResId;
//            switch (getItem(position) % 5) {
//                case 0:
//                    imageResId = R.drawable.img_nature1;
//                    break;
//                case 1:
//                    imageResId = R.drawable.img_nature2;
//                    break;
//                case 2:
//                    imageResId = R.drawable.img_nature3;
//                    break;
//                case 3:
//                    imageResId = R.drawable.img_nature4;
//                    break;
//                default:
//                    imageResId = R.drawable.img_nature5;
//            }
//
//            Bitmap bitmap = getBitmapFromMemCache(imageResId);
//            if (bitmap == null) {
//                bitmap = BitmapFactory.decodeResource(mContext.getResources(), imageResId);
//                addBitmapToMemoryCache(imageResId, bitmap);
//            }
//            viewHolder.imageView.setImageBitmap(bitmap);
//        }

        private void addBitmapToMemoryCache(int key, Bitmap bitmap) {
            if (getBitmapFromMemCache(key) == null) {
                mMemoryCache.put(key, bitmap);
            }
        }

        private Bitmap getBitmapFromMemCache(int key) {
            return mMemoryCache.get(key);
        }

        public class ViewHolder {
            TextView textView;
            CheckBox checkBox;
        }
    }
}
