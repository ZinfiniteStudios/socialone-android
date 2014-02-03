package com.socialone.android.fragment.twitter;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.socialone.android.R;
import com.socialone.android.utils.BlurTransformation;
import com.socialone.android.utils.Constants;
import com.socialone.android.utils.RoundTransformation;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.TitlePageIndicator;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by david.hodge on 1/13/14.
 */
public class TwitterProfileFragment extends SherlockFragment {

    View view;
    ListView listView;
    GoogleCardsAdapter googleCardsAdapter;
    SocialAuthAdapter mAuthAdapter;
    Location location;
    LocationManager locationManager;
    String lat;
    String lon;
    ViewPager viewPager;
    TitlePageIndicator titlePageIndicator;
    private ArrayList<Fragment> mFragments;
    private ArrayList<String> mtitles;
    FragmentManager fm;
    PagerAdapter pagerAdapter;
    TwitterAboutFragment twitterAboutFragment = new TwitterAboutFragment();
    TwitterFollowersFragment twitterFollowersFragment = new TwitterFollowersFragment();
    TwitterFollowingFragment twitterFollowingFragment = new TwitterFollowingFragment();

    ImageView userBackground;
    ImageView userProfile;
    TextView userName;
    TextView userRealName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.twitter_profile_header, container, false);

        userName = (TextView) view.findViewById(R.id.twitter_user_name_text);
        userRealName = (TextView) view.findViewById(R.id.twitter_real_name_text);
        userBackground = (ImageView) view.findViewById(R.id.twitter_background_image);
        userProfile = (ImageView) view.findViewById(R.id.twitter_profile_image);

        viewPager = (ViewPager) view.findViewById(R.id.social_view_pager);
        titlePageIndicator = (TitlePageIndicator) view.findViewById(R.id.social_tpi);

        mtitles = new ArrayList<String>();
        mtitles.add("About");
        mtitles.add("Followers");
        mtitles.add("Following");

        mFragments =  new ArrayList<Fragment>();
        mFragments.add(twitterAboutFragment);
        mFragments.add(twitterFollowersFragment);
        mFragments.add(twitterFollowingFragment);

        pagerAdapter = new PagerAdapter(getSherlockActivity(), mtitles, mFragments);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setCurrentItem(0);
        titlePageIndicator.setViewPager(viewPager);
        titlePageIndicator.setOnPageChangeListener(socialOPCL);
        titlePageIndicator.setOnCenterItemClickListener(new TitlePageIndicator.OnCenterItemClickListener() {
            @Override
            public void onCenterItemClick(int position) {
                Toast.makeText(getSherlockActivity(), "Center item " + Integer.toString(position), Toast.LENGTH_LONG).show();
            }
        });
        fm = getChildFragmentManager();

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
            lat = Double.toString(location.getLatitude());
            lon = Double.toString(location.getLongitude());
            twitterSetup();
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void setUpTwit4j(){
        try{
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(Constants.TWIT_CONSUMER_KEY)
                    .setOAuthConsumerSecret(Constants.TWIT_CONSUMER_SECRET)
                    .setOAuthAccessToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey())
                    .setOAuthAccessTokenSecret(mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            User user = twitter.showUser(twitter.getId());

            Picasso.with(getSherlockActivity())
                    .load(user.getOriginalProfileImageURL())
                    .resize(200, 200)
                    .centerCrop()
                    .transform(new RoundTransformation())
                    .into(userProfile);

            Picasso.with(getSherlockActivity())
                    .load(user.getProfileBannerURL())
                    .resize(2000, 2000)
                    .centerCrop()
                    .transform(new BlurTransformation(getSherlockActivity()))
                    .into(userBackground);

            userName.setText("@" + user.getScreenName());
            userRealName.setText(user.getName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void twitterSetup(){
        mAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                setUpTwit4j();
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
    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<Status> mFeed;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, List<Status> feed) {
            mContext = context;
            mFeed = feed;
        }

        public void setData(List<Status> feed){
            mFeed = feed;
        }

        @Override
        public boolean isEmpty() {
            return mShouldReturnEmpty && super.isEmpty();
        }

        @Override
        public int getCount() {
            return mFeed.size();
        }

        @Override
        public Status getItem(int position) {
            return mFeed.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.twitter_feed_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.social_checkin_name);
                viewHolder.userRealName = (TextView) view.findViewById(R.id.user_real_name);
                viewHolder.userTwitName = (TextView) view.findViewById(R.id.user_twitter_name);
                viewHolder.userImg = (ImageView) view.findViewById(R.id.user_image);
                viewHolder.postTime = (RelativeTimeTextView) view.findViewById(R.id.post_time);
                viewHolder.postClient = (TextView) view.findViewById(R.id.post_info_client);
                viewHolder.postUser = (TextView) view.findViewById(R.id.post_info_user);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final Status feed = getItem(position);
            viewHolder.textView.setText(feed.getText());
            viewHolder.userRealName.setText(feed.getUser().getName());
            viewHolder.userTwitName.setText("@" + feed.getUser().getScreenName());
            viewHolder.postTime.setReferenceTime(feed.getCreatedAt().getTime());
            //TODO add on click to these to open the respective client or user profile
            viewHolder.postClient.setText("via " + stripHtml(feed.getSource()));
//            viewHolder.postUser.setText("from " + feed.getUser().get);

            Picasso.with(mContext)
                    .load(feed.getUser().getProfileImageURL())
                    .resize(200, 200)
                    .centerCrop()
                    .into(viewHolder.userImg);

            return view;
        }

        public class ViewHolder {
            TextView textView;
            TextView userRealName;
            TextView userTwitName;
            RelativeTimeTextView postTime;
            TextView postClient;
            TextView postUser;
            ImageView userImg;
        }

        public String stripHtml(String html) {
            return Html.fromHtml(html).toString();
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {
        Context context;
        private LayoutInflater inflater;
        private ArrayList<String> titles;
        private ArrayList<Fragment> mFragments;

        public PagerAdapter(Context context, ArrayList<String> strings, ArrayList<Fragment> fragments){
            super(TwitterProfileFragment.this.getChildFragmentManager());
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        return;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.social_share_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_share:
//                SocialShareFragment.shareAllThings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
