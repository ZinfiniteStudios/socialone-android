package com.socialone.android.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.crashlytics.android.Crashlytics;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.socialone.android.R;
import com.socialone.android.appnet.adnlib.Annotations;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.data.Annotation;
import com.socialone.android.appnet.adnlib.data.Post;
import com.socialone.android.appnet.adnlib.response.PostResponseHandler;
import com.socialone.android.fragment.appnet.AppNetCheckInFragment;
import com.socialone.android.fragment.facebook.FacebookCheckInFragment;
import com.socialone.android.fragment.foursquare.FourSquareCheckInFragment;
import com.socialone.android.utils.Datastore;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import oauth.signpost.OAuth;

/**
 * Created with IntelliJ IDEA.
 * User: david.hodge
 * Date: 11/3/13
 * Time: 1:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class SocialCheckInFragment extends SherlockFragment {

    Context mContext;
    View view;
    Datastore mDatastore;
    //    @InjectView(R.id.checkin_list)
//    ListView checkInList;

    Dialog dialog;
    EditText checkinMessge;
    Button cancelCheckinBtn;
    Button checkinBtn;

    ViewPager viewPager;
    TitlePageIndicator titlePageIndicator;
    private ArrayList<Fragment> mFragments;
    private ArrayList<String> mtitles;
    FragmentManager fm;
    PagerAdapter pagerAdapter;
    GoogleCheckInFragment googleCheckInFragment = new GoogleCheckInFragment();
    FacebookCheckInFragment facebookCheckInFragment = new FacebookCheckInFragment();
    FourSquareCheckInFragment fourSquareCheckInFragment = new FourSquareCheckInFragment();
    AppNetCheckInFragment appNetCheckInFragment = new AppNetCheckInFragment();

    MapView mapView;
    Bundle bundle;
    GoogleAnalytics mGaInstance;
    Tracker mGaTracker;
    GoogleMap mMap;

    String lat;
    String lon;
    String address;
    String postalCode;
    String region;
    String countryCode;
    Location location;
    LocationManager locationManager;

    private UiLifecycleHelper uiHelper;
    Session session;

    AppDotNetClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;
        setHasOptionsMenu(true);

        mContext = getSherlockActivity();

        uiHelper = new UiLifecycleHelper(getSherlockActivity(), new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        });
        uiHelper.onCreate(savedInstanceState);
//        getSherlockActivity().setTheme();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.social_checkin_fragment, container, false);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewPager = (ViewPager) view.findViewById(R.id.checkin_view_pager);
        titlePageIndicator = (TitlePageIndicator) view.findViewById(R.id.checkin_tpi);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        //TODO add logic for only logged in accounts?
        mtitles = new ArrayList<String>();
        mFragments =  new ArrayList<Fragment>();

        if(prefs.getBoolean("facebook", false)){
            mtitles.add(getString(R.string.facebook));
            mFragments.add(facebookCheckInFragment);
        }

        if(prefs.getBoolean("foursquare", false)){
            mtitles.add(getString(R.string.four_square));
            mFragments.add(fourSquareCheckInFragment);
        }

        if(prefs.getBoolean("googleplus", false)){
            mtitles.add(getString(R.string.google_plus));
            mFragments.add(googleCheckInFragment);
        }

        if(prefs.getBoolean("appnet", false)){
            mtitles.add(getString(R.string.app_net));
            mFragments.add(appNetCheckInFragment);
        }

//        mtitles.add(getString(R.string.facebook));
//        mtitles.add(getString(R.string.four_square));
//        mtitles.add(getString(R.string.google_plus));
//        mtitles.add(getString(R.string.app_net));
//
//        mFragments.add(facebookCheckInFragment);
//        mFragments.add(fourSquareCheckInFragment);
//        mFragments.add(googleCheckInFragment);
//        mFragments.add(appNetCheckInFragment);

        pagerAdapter = new PagerAdapter(getSherlockActivity(), mtitles, mFragments);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        titlePageIndicator.setViewPager(viewPager);
        titlePageIndicator.setOnPageChangeListener(socialOPCL);
        titlePageIndicator.setOnCenterItemClickListener(new TitlePageIndicator.OnCenterItemClickListener() {
            @Override
            public void onCenterItemClick(int position) {
                checkinDialog();
            }
        });



        FragmentManager fm = getChildFragmentManager();

        mapView = (MapView) view.findViewById(R.id.checkin_map_fragment);
        mapView.onCreate(bundle);
        setUpMapIfNeeded(view);

        return view;
    }

    public void checkinDialog(){
//        final String placeName = place.getName();
//        final String placeId = place.getFactualId();
//        final String placeAdd = place.getAddress();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        client = new AppDotNetClient(prefs.getString(OAuth.OAUTH_TOKEN, null));
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
                list2.put("id", java.util.UUID.randomUUID());
                list2.put("address", address);
                list2.put("name", checkinMessge.getText().toString());
                list2.put("country_code", countryCode);
                list2.put("latitude", location.getLatitude());
                list2.put("longitude", location.getLongitude());
                list2.put("postcode", postalCode);
                list2.put("region", region);


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

        dialog.setTitle("Custom Checkin");
        dialog.show();
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getSherlockActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                address = addresses.get(0).getAddressLine(0);
                Log.d("appnet", address);
                postalCode = addresses.get(0).getPostalCode();
                region = addresses.get(0).getAdminArea();
                countryCode = addresses.get(0).getCountryCode();
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    private void getUserLocation() {
        try {

            locationManager = (LocationManager) getSherlockActivity().getSystemService(Context.LOCATION_SERVICE);
            String bestProvider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(bestProvider);
            lat = Double.toString(location.getLatitude());
            lon = Double.toString(location.getLongitude());
            getCompleteAddressString(location.getLatitude(), location.getLongitude());
            final CameraPosition HOME =
                    new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(17)
                            .bearing(-100)
                            .tilt(25)
                            .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(HOME));
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void setUpMap() {
        mMap.setIndoorEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        getUserLocation();
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

    class PagerAdapter extends FragmentPagerAdapter {
        Context context;
        private LayoutInflater inflater;
        private ArrayList<String> titles;
        private ArrayList<Fragment> mFragments;

        public PagerAdapter(Context context, ArrayList<String> strings, ArrayList<Fragment> fragments){
            super(SocialCheckInFragment.this.getChildFragmentManager());
            this.context = context;
            this.titles = strings;
            this.mFragments = fragments;
            this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return this.titles.size();

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        public void setTitles(ArrayList<String> titles) {
            this.titles = titles;
        }

        public void setFragments(ArrayList<Fragment> fragments) {
            this.mFragments = fragments;
        }
    }

    private ViewPager.OnPageChangeListener socialOPCL = new ViewPager.OnPageChangeListener(){
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Toast.makeText(mContext, "connected", Toast.LENGTH_SHORT).show();
        } else if (state.isClosed()) {
            Toast.makeText(mContext, "error",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(getActivity()); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(getActivity()); // Add this method.
    }
}
