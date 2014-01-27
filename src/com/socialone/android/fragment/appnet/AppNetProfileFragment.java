package com.socialone.android.fragment.appnet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.data.Post;
import com.socialone.android.appnet.adnlib.data.PostList;
import com.socialone.android.appnet.adnlib.data.User;
import com.socialone.android.appnet.adnlib.response.PostListResponseHandler;
import com.socialone.android.appnet.adnlib.response.PostResponseHandler;
import com.socialone.android.appnet.adnlib.response.UserResponseHandler;
import com.socialone.android.utils.BlurTransformation;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;

import oauth.signpost.OAuth;

/**
 * Created by david.hodge on 1/14/14.
 */
public class AppNetProfileFragment extends SherlockFragment {

    View view;
    ListView listView;
    AppDotNetClient client;
    GoogleCardsAdapter googleCardsAdapter;
    ViewPager viewPager;
    TitlePageIndicator titlePageIndicator;
    private ArrayList<Fragment> mFragments;
    private ArrayList<String> mtitles;
    FragmentManager fm;
    PagerAdapter pagerAdapter;

    AppNetFollowersFragment appNetFollowersFragment = new AppNetFollowersFragment();
    AppNetFollowingFragment appNetFollowingFragment = new AppNetFollowingFragment();
    ImageView userBackground;
    ImageView userProfile;
    TextView userName;
    TextView userRealName;
    TextView userPosts;
    TextView userFollowing;
    TextView userFollowers;
    TextView userFavorites;
    TextView userDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.appnet_profile_header, container, false);

        userName = (TextView) view.findViewById(R.id.appnet_user_name_text);
        userRealName = (TextView) view.findViewById(R.id.appnet_real_name_text);
        userPosts = (TextView) view.findViewById(R.id.appnet_post_count_text);
        userFollowing = (TextView) view.findViewById(R.id.appnet_following_count_text);
        userFollowers = (TextView) view.findViewById(R.id.appnet_follower_count_text);
        userFavorites = (TextView) view.findViewById(R.id.appnet_fav_count_text);
        userDescription = (TextView) view.findViewById(R.id.appnet_desc_text);
        userBackground = (ImageView) view.findViewById(R.id.appnet_background_image);
        userProfile = (ImageView) view.findViewById(R.id.appnet_profile_image);

        viewPager = (ViewPager) view.findViewById(R.id.social_view_pager);
        titlePageIndicator = (TitlePageIndicator) view.findViewById(R.id.social_tpi);

        mtitles = new ArrayList<String>();
        mtitles.add("Followers");
        mtitles.add("Following");

        mFragments =  new ArrayList<Fragment>();
        mFragments.add(appNetFollowersFragment);
        mFragments.add(appNetFollowingFragment);

        pagerAdapter = new PagerAdapter(getSherlockActivity(), mtitles, mFragments);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(5);
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
        getAppNetInfo();
//        getAppNetFeed();

    }

    private void getAppNetInfo(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        client = new AppDotNetClient(prefs.getString(OAuth.OAUTH_TOKEN, null));
        client.retrieveCurrentUser(new UserResponseHandler() {
            @Override
            public void onSuccess(final User responseData) {
//                final User data = responseData;
                getSherlockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        Picasso.with(getSherlockActivity())
                                .load(responseData.getAvatarImage().getUrl())
                                .resize(200, 200)
                                .centerCrop()
                                .into(userProfile);

                        Picasso.with(getSherlockActivity())
                                .load(responseData.getCoverImage().getUrl())
                                .resize(2000, 2000)
                                .centerCrop()
                                .transform(new BlurTransformation(getSherlockActivity()))
                                .into(userBackground);

                        userName.setText(responseData.getUsername());
//                        userRealName.setText(responseData.getType());
                        userPosts.setText(responseData.getCounts().getPosts() + " post");
                        userFollowing.setText(responseData.getCounts().getFollowing() + " following");
                        userFollowers.setText(responseData.getCounts().getFollowers() + " followers");
                        userFavorites.setText(responseData.getCounts().getStars() + " stars");
                        userDescription.setText(responseData.getDescription().getText());
                    }
                });

            }

        });
    }

    private void getAppNetFeed(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        client = new AppDotNetClient(prefs.getString(OAuth.OAUTH_TOKEN, null));
        client.retrieveUnifiedStream(new PostListResponseHandler() {
            @Override
            public void onSuccess(PostList responseData) {
                final ArrayList<Post> places = responseData;
                getSherlockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), places);
                        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
                        swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                        swingBottomInAnimationAdapter.setAbsListView(listView);
                        listView.setAdapter(swingBottomInAnimationAdapter);
                        googleCardsAdapter.setData(places);
                    }
                });
            }
        });
    }


    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Post> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, ArrayList<Post> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(ArrayList<Post> appPlace){
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
        public Post getItem(int position) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.appnet_feed_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.social_checkin_name);
                viewHolder.userImg = (ImageView) view.findViewById(R.id.user_image);
                viewHolder.repostPost = (ImageView) view.findViewById(R.id.repost_post);
                viewHolder.starPost = (ImageView) view.findViewById(R.id.star_post);
                viewHolder.postTime = (RelativeTimeTextView) view.findViewById(R.id.post_time);
                viewHolder.postClient = (TextView) view.findViewById(R.id.post_info_client);
                viewHolder.postUser = (TextView) view.findViewById(R.id.post_info_user);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final Post post = getItem(position);
            viewHolder.textView.setText(post.getText());
            viewHolder.postTime.setReferenceTime(post.getCreatedAt().getTime());
            //TODO add on click to these to open the respective client or user profile
            viewHolder.postClient.setText("via " + post.getSource().getName());
            viewHolder.postUser.setText("from " + post.getUser().getName());

            Picasso.with(mContext)
                    .load(post.getUser().getAvatarImage().getUrl())
                    .resize(200, 200)
                    .centerCrop()
                    .into(viewHolder.userImg);

            viewHolder.starPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    client.starPost(post.getId(), new PostResponseHandler() {
                        @Override
                        public void onSuccess(Post responseData) {
                            Log.d("post", "post has been starred!");
                        }
                    });
                }
            });

            viewHolder.repostPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    client.repostPost(post.getId(), new PostResponseHandler() {
                        @Override
                        public void onSuccess(Post responseData) {
                            Log.d("post", "post has been reposted!");
                        }
                    });
                }
            });
//            setImageView(viewHolder, position);

            return view;
        }

        public class ViewHolder {
            TextView textView;
            RelativeTimeTextView postTime;
            TextView postClient;
            TextView postUser;
            ImageView userImg;
            ImageView starPost;
            ImageView repostPost;
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
            super(AppNetProfileFragment.this.getChildFragmentManager());
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
