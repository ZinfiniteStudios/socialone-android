package com.socialone.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.commonsware.cwac.merge.MergeAdapter;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.activity.Item;
import com.googlecode.flickrjandroid.people.User;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.data.Interaction;
import com.socialone.android.appnet.adnlib.data.InteractionList;
import com.socialone.android.appnet.adnlib.response.InteractionListResponseHandler;
import com.socialone.android.condesales.EasyFoursquareAsync;
import com.socialone.android.condesales.listeners.AccessTokenRequestListener;
import com.socialone.android.condesales.listeners.GetNotificationsListener;
import com.socialone.android.condesales.models.Notifications;
import com.socialone.android.utils.Constants;
import com.socialone.android.utils.FlickrHelper;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oauth.signpost.OAuth;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by david.hodge on 1/6/14.
 */
public class NotificationFragment extends SherlockFragment {

    View view;
    ListView listView;
    GoogleCardsAdapter googleCardsAdapter;
    AppCardsAdapter appCardsAdapter;
    FourCardsAdapter fourCardsAdapter;
    FlickrCardsAdapter flickrCardsAdapter;
    AppDotNetClient client;
    SocialAuthAdapter mAuthAdapter;
    SocialAuthAdapter mFlickrAdapter;
    private UiLifecycleHelper uiHelper;
    Session session;
    EasyFoursquareAsync easyFoursquareAsync;
    MergeAdapter mergeAdapter;
    Flickr f;
    User user;

    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapterApp;
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapterFour;
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapterFlickr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getSherlockActivity(), new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        });
        uiHelper.onCreate(savedInstanceState);
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
        mergeAdapter = new MergeAdapter();
//        flickrCardsAdapter = new FlickrCardsAdapter(getSherlockActivity());
//        swingBottomInAnimationAdapterFlickr = new SwingBottomInAnimationAdapter(flickrCardsAdapter);
//        googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity());
//        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
//        fourCardsAdapter = new FourCardsAdapter(getSherlockActivity());
//        swingBottomInAnimationAdapterFour = new SwingBottomInAnimationAdapter(fourCardsAdapter);
//        appCardsAdapter = new AppCardsAdapter(getSherlockActivity());
//        swingBottomInAnimationAdapterApp = new SwingBottomInAnimationAdapter(appCardsAdapter);
        twitterSetup();
//        getAppNetFeed();
//        getFacebookFeed();
//        fourSquareNotifications();
//        flickrSetup();
//        mergeAdapter.addAdapter(swingBottomInAnimationAdapter);
//        mergeAdapter.addAdapter(swingBottomInAnimationAdapterFlickr);
//        mergeAdapter.addAdapter(swingBottomInAnimationAdapterApp);
//        mergeAdapter.addAdapter(swingBottomInAnimationAdapterFour);
//        listView.setAdapter(mergeAdapter);
    }

    private void flickrSetup(){
        mFlickrAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Log.d("flickr", "flickr complete");
                f = FlickrHelper.getInstance().getFlickrAuthed(
                        mAuthAdapter.getCurrentProvider().getAccessGrant().getKey(),
                        mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
                Log.d("flickr", "flickr helper complete");
//                f = new Flickr();
//                auth = new OAuth();
//                auth.setToken(new OAuthToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey(), mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret()));
                try{
                    Log.d("flickr", "setting feed");
                    setUpFeed();
                }catch (Exception e){
                    Log.d("flickr", e.toString());
                }
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
        mFlickrAdapter.addCallBack(SocialAuthAdapter.Provider.FLICKR, Constants.FLICKR_CALLBACK);
        mFlickrAdapter.authorize(getSherlockActivity(), SocialAuthAdapter.Provider.FLICKR);
    }

    private void setUpFeed() throws Exception{
        user = new User();
        String userName = user.getRealName();
//        Log.d("flickr", userName);
        Date day = null;
//        List<Photo> photos = f.getInterestingnessInterface().getList(day, Constants.EXTRAS, Constants.FETCH_PER_PAGE, 1);
        List<Item> photos = f.getActivityInterface().userPhotos(30, 1, "100d");
        swingBottomInAnimationAdapterFlickr.setInitialDelayMillis(300);
        swingBottomInAnimationAdapterFlickr.setAbsListView(listView);
        mergeAdapter.addAdapter(swingBottomInAnimationAdapterFlickr);
        Log.d("adapter", "flickr added");
        flickrCardsAdapter.setData(photos);
        mergeAdapter.notifyDataSetChanged();
        swingBottomInAnimationAdapterFlickr.notifyDataSetChanged();
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
            List<Status> statuses = twitter.getMentionsTimeline();
            googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity());
            swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
            swingBottomInAnimationAdapter.setInitialDelayMillis(300);
            swingBottomInAnimationAdapter.setAbsListView(listView);
            Log.d("adapter", "twitter added");
            googleCardsAdapter.setData(statuses);
//            mergeAdapter.addAdapter(swingBottomInAnimationAdapter);
            listView.setAdapter(swingBottomInAnimationAdapter);
            mergeAdapter.notifyDataSetChanged();
            swingBottomInAnimationAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fourSquareNotifications(){
        easyFoursquareAsync = new EasyFoursquareAsync(getSherlockActivity());
        easyFoursquareAsync.requestAccess(new AccessTokenRequestListener() {
            @Override
            public void onAccessGrant(String accessToken) {
                easyFoursquareAsync.getUserNotifications(new GetNotificationsListener() {
                    @Override
                    public void onGotNotifications(ArrayList<Notifications> list) {
                        Log.d("foursquare", "notification response " + list.toString());
                        final ArrayList<Notifications> list1 = list;
                        getSherlockActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                        swingBottomInAnimationAdapterFour.setInitialDelayMillis(300);
                        swingBottomInAnimationAdapterFour.setAbsListView(listView);
                        Log.d("adapter", "4sq added");
                        fourCardsAdapter.setData(list1);
                                mergeAdapter.notifyDataSetChanged();
                                swingBottomInAnimationAdapterFour.notifyDataSetChanged();
                            }
                      });
                    }

                    @Override
                    public void onError(String errorMsg) {
                        Log.d("foursquare", "notification error " + errorMsg.toString());
                    }
                });
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
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

    private void getAppNetFeed(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        client = new AppDotNetClient(prefs.getString(OAuth.OAUTH_TOKEN, null));
        client.retrieveCurrentUserInteractions(new InteractionListResponseHandler() {
            @Override
            public void onSuccess(InteractionList responseData) {
                final ArrayList<Interaction> interactions = responseData;
                getSherlockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swingBottomInAnimationAdapterApp.setInitialDelayMillis(300);
                        swingBottomInAnimationAdapterApp.setAbsListView(listView);
                        Log.d("adapter", "app.net added");
//                        listView.setAdapter(mergeAdapter);
                        appCardsAdapter.setData(interactions);
                        mergeAdapter.notifyDataSetChanged();
                        swingBottomInAnimationAdapterApp.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void getFacebookFeed(){
        session = ensureFacebookSessionFromCache(getSherlockActivity());

        if (session != null) {

            Request.Callback callback = new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    Log.d("faceboook", response.toString());
                }
            };

            Bundle bundle = new Bundle();
            bundle.putString("include_read", "true");
            Request request = new Request(session, "/me/notifications", bundle, HttpMethod.GET, callback);
            request.executeAsync();

        }

    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<Status> mFeed;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context) {
            mContext = context;
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

    public class AppCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Interaction> mFeed;
        private boolean mShouldReturnEmpty = true;

        public AppCardsAdapter(Context context) {
            mContext = context;
        }

        public void setData(ArrayList<Interaction> feed){
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
        public Interaction getItem(int position) {
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

            final Interaction feed = getItem(position);
            viewHolder.textView.setText(feed.getAction());
//            viewHolder.userRealName.setText(feed.getUser().getName());
//            viewHolder.userTwitName.setText("@" + feed.getUser().getScreenName());
            viewHolder.postTime.setReferenceTime(feed.getEventDate().getTime());
            //TODO add on click to these to open the respective client or user profile
//            viewHolder.postClient.setText("via " + stripHtml(feed.getSource()));
//            viewHolder.postUser.setText("from " + feed.getUser().get);

//            Picasso.with(mContext)
//                    .load(feed.getUser().getProfileImageURL())
//                    .resize(200, 200)
//                    .centerCrop()
//                    .into(viewHolder.userImg);

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

    public class FourCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Notifications> mFeed;
        private boolean mShouldReturnEmpty = true;

        public FourCardsAdapter(Context context) {
            mContext = context;
        }

        public void setData(ArrayList<Notifications> feed){
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
        public Notifications getItem(int position) {
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

            final Notifications feed = getItem(position);
            viewHolder.textView.setText(feed.getText());
//            viewHolder.userRealName.setText(feed.getUser().getName());
//            viewHolder.userTwitName.setText("@" + feed.getUser().getScreenName());
//            viewHolder.postTime.setReferenceTime(feed.getEventDate().getTime());
            //TODO add on click to these to open the respective client or user profile
//            viewHolder.postClient.setText("via " + stripHtml(feed.getSource()));
//            viewHolder.postUser.setText("from " + feed.getUser().get);

//            Picasso.with(mContext)
//                    .load(feed.getUser().getProfileImageURL())
//                    .resize(200, 200)
//                    .centerCrop()
//                    .into(viewHolder.userImg);

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

    public class FlickrCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<Item> mFeed;
        private boolean mShouldReturnEmpty = true;

        public FlickrCardsAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<Item> feed){
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
        public Item getItem(int position) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.flickr_feed_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.flickrName = (TextView) view.findViewById(R.id.flickr_name);
                viewHolder.flickrExtra = (RelativeTimeTextView) view.findViewById(R.id.flickr_extra);
                viewHolder.flickrImage = (ImageView) view.findViewById(R.id.flickr_imageview);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final Item feed = getItem(position);
            viewHolder.flickrName.setText(feed.getTitle());
            viewHolder.flickrExtra.setText(Integer.toString(feed.getViews()));
//            viewHolder.flickrExtra.setReferenceTime(feed.getDateAdded().getTime());
            try{
                Log.d("flickr", Integer.toString(feed.getViews()));
            }catch (Exception e){
                Log.d("flickr", e.toString());
            }
//            viewHolder.flickrExtra.setText(feed.getId());
            Picasso.with(mContext)
                    .load(feed.getType())
                    .into(viewHolder.flickrImage);

            return view;
        }

        public class ViewHolder {
            TextView flickrName;
            RelativeTimeTextView flickrExtra;
            ImageView flickrImage;
        }

        public String stripHtml(String html) {
            return Html.fromHtml(html).toString();
        }
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
//            Toast.makeText(getSherlockActivity(), "connected", Toast.LENGTH_SHORT).show();
//            getUserInfo();
            Request meRequest = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        Toast.makeText(getSherlockActivity(), "Welcome back " + user.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            meRequest.executeAndWait();
        } else if (state.isClosed()) {
            Toast.makeText(getSherlockActivity(), "error",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(getSherlockActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(getSherlockActivity());
    }

    public static Session ensureFacebookSessionFromCache(Context context){
        Session activeSession = Session.getActiveSession();
        if (activeSession == null || !activeSession.getState().isOpened()) {
            activeSession = Session.openActiveSessionFromCache(context);
        }
        return activeSession;
    }
}
