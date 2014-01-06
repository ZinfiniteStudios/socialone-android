package com.socialone.android.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;

import com.actionbarsherlock.app.ActionBar;
import com.amazon.insights.AmazonInsights;
import com.amazon.insights.InsightsCredentials;
import com.amazon.insights.InsightsOptions;
import com.crashlytics.android.Crashlytics;
import com.crittercism.app.Crittercism;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.socialone.android.MainApp;
import com.socialone.android.R;
import com.socialone.android.fragment.SocialCheckInFragment;
import com.socialone.android.fragment.SocialShareFragment;

import roboguice.inject.InjectView;

public class StartupActivity extends RoboSherlockFragmentActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

    @InjectView(R.id.pager)ViewPager pager;
    ActionBar bar;
    private AmazonInsights insights;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        Crittercism.initialize(getApplicationContext(), getResources().getString(R.string.critter_id));
//        BugSenseHandler.initAndStartSession(getApplicationContext(), getString(R.string.bugsense_id));
        ParseAnalytics.trackAppOpened(getIntent());
        InsightsCredentials credentials = AmazonInsights.newCredentials(getString(R.string.amazon_key), getString(R.string.amazon_private_key));
        InsightsOptions options = AmazonInsights.newOptions(true, true);
        insights = AmazonInsights.newInstance(credentials, getApplicationContext(), options);
//        Ubertesters.initialize(getApplication());
        Log.i(MainApp.TAG, "onCreate");
        setContentView(R.layout.startup_tabs);

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        bar = getSupportActionBar();
        bar.addTab(bar.newTab().setText("Share").setTabListener(this).setTag(SocialShareFragment.class.getName()));
        bar.addTab(bar.newTab().setText("Check-In").setTabListener(this).setTag(SocialCheckInFragment.class.getName()));
//        bar.addTab(bar.newTab().setText("Tab C").setTabListener(this).setTag(SocialConnectFragment.class.getName()));
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setTitle("Social One");
//        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        bar.setSelectedNavigationItem(0);
        pager.setAdapter(new OakAdapter(getSupportFragmentManager()));
        pager.setOnPageChangeListener(this);

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {}

    @Override
    public void onPageSelected(int i) {
        bar.setSelectedNavigationItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {}

    private class OakAdapter extends FragmentPagerAdapter{

        public OakAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return Fragment.instantiate(StartupActivity.this, bar.getTabAt(i).getTag().toString(), null);
        }

        @Override
        public int getCount() {
            return bar.getTabCount();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = this.getWindow();

        // Eliminates color banding
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this); // Add this method.
//        BugSenseHandler.startSession(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this); // Add this method.
//        BugSenseHandler.closeSession(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.insights.getSessionClient().resumeSession();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.insights.getSessionClient().pauseSession();
        this.insights.getEventClient().submitEvents();
    }


}

