package com.socialone.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.amazon.insights.AmazonInsights;
import com.amazon.insights.InsightsCredentials;
import com.amazon.insights.InsightsOptions;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.socialone.android.R;
import com.socialone.android.fragment.AboutFragment;
import com.socialone.android.fragment.AppNetFeedFragment;
import com.socialone.android.fragment.FacebookMainFeedFragment;
import com.socialone.android.fragment.FourSquareFeedFragment;
import com.socialone.android.fragment.SocialFragment;
import com.socialone.android.fragment.TwitterMainFeedFragment;
import com.socialone.android.utils.BlurTransformation;
import com.socialone.android.utils.Constants;
import com.socialone.android.utils.OldBlurTransformation;
import com.socialone.android.utils.RoundTransformation;
import com.socialone.android.viewcomponents.NavDrawerItem;
import com.squareup.picasso.Picasso;

import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

//import com.amazon.device.ads.AdLayout;
//import com.amazon.device.ads.AdTargetingOptions;

/**
 * Created by david.hodge on 12/18/13.
 */
public class MainActivity extends SherlockFragmentActivity implements DrawerLayout.DrawerListener {

    DrawerLayout mDrawerLayout;
    FrameLayout mContent;
    ImageView userImage;
    ImageView userBackground;
    TextView userNameText;
    TextView userLocationText;


    ActionBarDrawerToggle mActionBarDrawerToggle;
    FragmentManager mfragmentManager;
    NavDrawerItem currentNavigationDrawerItem;
    Context mContext;
    FragmentTransaction ft;
    private AmazonInsights insights;

//    AdLayout adLayout;
//    AdTargetingOptions adTargetingOptions;

    String userShareExtra;

    private UiLifecycleHelper uiHelper;
    Session session;
    String userProfileImageLink;
    String userHeaderImageLink;
    String userName;
    String userLocation;

    public static final int NAV_SHARE = R.id.nav_item_share;
    public static final int NAV_FACEBOOK = R.id.nav_item_facebook;
    public static final int NAV_ID_TEST_2 = R.id.nav_item_twitter;
    public static final int NAV_ID_TEST_3 = R.id.nav_item_myspace;
    public static final int NAV_APP_NET = R.id.nav_item_appnet;
    public static final int NAV_TWITTER = R.id.nav_item_twitter;
    public static final int NAV_FOURSQUARE = R.id.nav_item_foursquare;
    public static final int NAV_ABOUT = R.id.nav_item_about;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InsightsCredentials credentials = AmazonInsights.newCredentials(getString(R.string.amazon_key), getString(R.string.amazon_private_key));
        InsightsOptions options = AmazonInsights.newOptions(true, true);
        insights = AmazonInsights.newInstance(credentials, getApplicationContext(), options);

        setContentView(R.layout.main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_root);
        mContent = (FrameLayout) findViewById(R.id.fragment_container);

        userImage = (ImageView) findViewById(R.id.user_profile_image);
        userBackground = (ImageView) findViewById(R.id.user_background_image);
        userNameText = (TextView) findViewById(R.id.user_name);
        userLocationText = (TextView) findViewById(R.id.user_location);

//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("Park", "BGW");
//        testObject.saveInBackground();

        mContext = this;
        mfragmentManager = getSupportFragmentManager();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.app_name));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(android.R.color.transparent);

//        // Get the intent that started this activity
//        Intent intent = getIntent();
//        Uri data = intent.getData();
//
//        // Figure out what to do based on the intent type
//        if (intent.getType().indexOf("image/") != -1) {
//            // Handle intents with image data ...
//        } else if (intent.getType().equals("text/plain")) {
//            setContentFragment(NAV_SHARE);
//            userShareExtra = data.getUserInfo();
//        }

        uiHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        });
        uiHelper.onCreate(savedInstanceState);

        initDrawerLayout();
        getUserInfo();
        if (savedInstanceState == null) {
            setContentFragment(NAV_SHARE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void initDrawerLayout() {
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_navigation_drawer,
                R.string.drawer_open, R.string.drawer_close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    private void getUserInfo() {

        Log.d("image", "getting info");

        session = ensureFacebookSessionFromCache(mContext);
        Request meRequest = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    userProfileImageLink = Constants.FACEBOOK_GRAPH + user.getId() + "/picture?type=large";
                    userHeaderImageLink = Constants.FACEBOOK_GRAPH + user.getId() + "/picture?type=large";
                    userName = user.getName();
                    if(user.getLocation() != null){
                        userLocation = user.getLocation().getCity() + " " + user.getLocation().getState();
                    }else{
                        userLocation = "Location Unavail";
                    }

                    //sets user information
                    userNameText.setText(userName);
                    userLocationText.setText(userLocation);

                    //displays user's profile image
                    Picasso.with(mContext)
                            .load(userProfileImageLink)
                            .resize(200, 200)
                            .centerCrop()
                            .transform(new RoundTransformation())
                            .into(userImage);

                    //use large banner image if available
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        Picasso.with(mContext)
                                .load(userHeaderImageLink)
                                .resize(400, 400)
                                .centerCrop()
                                .transform(new BlurTransformation(mContext))
                                .into(userBackground);
                    } else {
                        Picasso.with(mContext)
                                .load(userHeaderImageLink)
                                .resize(400, 400)
                                .centerCrop()
                                .transform(new OldBlurTransformation())
                                .into(userBackground);
                    }
                }
            }
        });
        meRequest.executeAsync();

//        //displays user's profile image
//        Picasso.with(mContext)
//                .load(userProfileImageLink)
//                .resize(200, 200)
//                .centerCrop()
//                .transform(new RoundTransformation())
//                .into(userImage);
//
//        //use large banner image if available
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            Picasso.with(mContext)
//                    .load(userHeaderImageLink)
//                    .resize(400, 400)
//                    .centerCrop()
//                    .transform(new BlurTransformation(mContext))
//                    .into(userBackground);
//        } else {
//            Picasso.with(mContext)
//                    .load(userHeaderImageLink)
//                    .resize(400, 400)
//                    .centerCrop()
//                    .transform(new OldBlurTransformation())
//                    .into(userBackground);
//        }
    }


    public void navigationDrawerItemClick(View v) {
        setSupportProgressBarIndeterminateVisibility(false);
        switch (v.getId()) {
            default:
                if (currentNavigationDrawerItem != v) {
                    mDrawerLayout.closeDrawers();
                    setContentFragment(v.getId());
                }
        }
    }

    private void setContentFragment(int fragID) {
        RoboInjector injector = RoboGuice.getInjector(this);
        Fragment fragment;
        Bundle args = new Bundle();
        switch (fragID) {
            case NAV_SHARE:
                fragment = new SocialFragment();
                break;
            case NAV_FACEBOOK:
//                fragment = injector.getInstance(FacebookMainFeedFragment.class);
                fragment = new FacebookMainFeedFragment();
                break;
            case NAV_ID_TEST_3:
//                fragment = injector.getInstance(SocialFragment.class);
                fragment = new SocialFragment();
                break;
            case NAV_APP_NET:
//                fragment = injector.getInstance(AppNetFeedFragment.class);
                fragment = new AppNetFeedFragment();
                break;
            case NAV_TWITTER:
//                fragment = injector.getInstance(TwitterMainFeedFragment.class);
                fragment = new TwitterMainFeedFragment();
                break;
            case NAV_FOURSQUARE:
//                fragment = injector.getInstance(FourSquareFeedFragment.class);
                fragment = new FourSquareFeedFragment();
                break;
            case NAV_ABOUT:
//                fragment = injector.getInstance(AboutFragment.class);
                fragment = new AboutFragment();
                break;
            default:
                return;
        }

        if (fragment != null) {
            fragment.setArguments(args);
            fragment.setRetainInstance(true);
        }

        ft = mfragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.fragment_container, fragment).addToBackStack("tag");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        uiHelper.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
//        getFragmentManager().getBackStackEntryCount();
        if (mfragmentManager.getBackStackEntryCount() == 1) {
            this.finish();
        }
        mfragmentManager.popBackStack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(getMenuItem(item))) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
//        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private android.view.MenuItem getMenuItem(final MenuItem item) {
        return new android.view.MenuItem() {
            @Override
            public int getItemId() {
                return item.getItemId();
            }

            public boolean isEnabled() {
                return true;
            }

            @Override
            public boolean collapseActionView() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean expandActionView() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public ActionProvider getActionProvider() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public View getActionView() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public char getAlphabeticShortcut() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getGroupId() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Drawable getIcon() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Intent getIntent() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ContextMenu.ContextMenuInfo getMenuInfo() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public char getNumericShortcut() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getOrder() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public SubMenu getSubMenu() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public CharSequence getTitle() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public CharSequence getTitleCondensed() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean hasSubMenu() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isActionViewExpanded() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isCheckable() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isChecked() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isVisible() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public android.view.MenuItem setActionProvider(ActionProvider actionProvider) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setActionView(View view) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setActionView(int resId) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setAlphabeticShortcut(char alphaChar) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setCheckable(boolean checkable) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setChecked(boolean checked) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setEnabled(boolean enabled) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setIcon(Drawable icon) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setIcon(int iconRes) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setIntent(Intent intent) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setNumericShortcut(char numericChar) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setShortcut(char numericChar, char alphaChar) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setShowAsAction(int actionEnum) {
                // TODO Auto-generated method stub

            }

            @Override
            public android.view.MenuItem setShowAsActionFlags(int actionEnum) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setTitle(CharSequence title) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setTitle(int title) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setTitleCondensed(CharSequence title) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setVisible(boolean visible) {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this); // Add this method.
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        this.insights.getSessionClient().resumeSession();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        this.insights.getSessionClient().pauseSession();
        this.insights.getEventClient().submitEvents();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
//            Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
//            getUserInfo();
        } else if (state.isClosed()) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDrawerSlide(View view, float v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDrawerOpened(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDrawerClosed(View view) {
        navigationDrawerItemClick(view);
    }

    @Override
    public void onDrawerStateChanged(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static Session ensureFacebookSessionFromCache(Context context){
        Session activeSession = Session.getActiveSession();
        if (activeSession == null || !activeSession.getState().isOpened()) {
            activeSession = Session.openActiveSessionFromCache(context);
        }
        return activeSession;
    }
}
