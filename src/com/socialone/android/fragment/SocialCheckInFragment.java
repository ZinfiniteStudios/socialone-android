package com.socialone.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crittercism.app.Crittercism;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.inject.Inject;
import com.socialone.android.R;
import com.socialone.android.utils.Datastore;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: david.hodge
 * Date: 11/3/13
 * Time: 1:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class SocialCheckInFragment extends RoboSherlockFragment {

    Context mContext;
    View view;
    @Inject
    Datastore mDatastore;
    //    @InjectView(R.id.checkin_list)
//    ListView checkInList;

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
    Location location;
    LocationManager locationManager;

    private UiLifecycleHelper uiHelper;
    Session session;


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

        //TODO add logic for only logged in accounts?
        mtitles = new ArrayList<String>();
        mtitles.add(getString(R.string.facebook));
        mtitles.add(getString(R.string.four_square));
        mtitles.add(getString(R.string.google_plus));
        mtitles.add(getString(R.string.app_net));

        mFragments =  new ArrayList<Fragment>();
        mFragments.add(facebookCheckInFragment);
        mFragments.add(fourSquareCheckInFragment);
        mFragments.add(googleCheckInFragment);
        mFragments.add(appNetCheckInFragment);

        pagerAdapter = new PagerAdapter(getSherlockActivity(), mtitles, mFragments);

        viewPager.setAdapter(pagerAdapter);
        titlePageIndicator.setViewPager(viewPager);
        titlePageIndicator.setOnPageChangeListener(socialOPCL);
        FragmentManager fm = getChildFragmentManager();

        mapView = (MapView) view.findViewById(R.id.checkin_map_fragment);
        mapView.onCreate(bundle);
        setUpMapIfNeeded(view);

        return view;
    }

    private void getUserLocation() {
        try {

            locationManager = (LocationManager) getSherlockActivity().getSystemService(Context.LOCATION_SERVICE);
            String bestProvider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(bestProvider);
            lat = Double.toString(location.getLatitude());
            lon = Double.toString(location.getLongitude());

            final CameraPosition HOME =
                    new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(17)
                            .bearing(-100)
                            .tilt(25)
                            .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(HOME));
        } catch (Exception e) {
            e.printStackTrace();
            Crittercism.logHandledException(e);
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
