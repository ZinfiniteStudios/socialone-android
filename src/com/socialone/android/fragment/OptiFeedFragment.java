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
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.parse.signpost.OAuth;
import com.socialone.android.R;
import com.socialone.android.api.facebook.NewsFeedItem;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.data.Post;
import com.socialone.android.appnet.adnlib.data.PostList;
import com.socialone.android.appnet.adnlib.response.PostListResponseHandler;
import com.socialone.android.appnet.adnlib.response.PostResponseHandler;
import com.socialone.android.utils.Constants;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by david.hodge on 12/25/13.
 */
public class OptiFeedFragment extends SherlockFragment {

    View view;
    ListView listView;
    MergeAdapter mergeAdapter;
    FbCardsAdapter fbCardsAdapter;
    AppCardsAdapter appCardsAdapter;
    TwitCardsAdapter twitCardsAdapter;
    AppDotNetClient client;
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapterFb;
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapterApp;
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapterTwit;
    SocialAuthAdapter mAuthAdapter;
    private UiLifecycleHelper uiHelper;
    Session session;
    Gson gson;

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
        gson = new Gson();
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
        getFacebookFeed();
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
            List<Status> statuses = twitter.getHomeTimeline();
            twitCardsAdapter = new TwitCardsAdapter(getSherlockActivity(), statuses);
            swingBottomInAnimationAdapterTwit = new SwingBottomInAnimationAdapter(twitCardsAdapter);
            swingBottomInAnimationAdapterTwit.setInitialDelayMillis(300);
            swingBottomInAnimationAdapterTwit.setAbsListView(listView);
            mergeAdapter.addAdapter(swingBottomInAnimationAdapterTwit);
            listView.setAdapter(mergeAdapter);
            twitCardsAdapter.setData(statuses);
            Log.d("twitter", "twitter4j " + statuses.toString());
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
                        appCardsAdapter = new AppCardsAdapter(getSherlockActivity(), places);
                        swingBottomInAnimationAdapterApp = new SwingBottomInAnimationAdapter(appCardsAdapter);
                        swingBottomInAnimationAdapterApp.setInitialDelayMillis(300);
                        swingBottomInAnimationAdapterApp.setAbsListView(listView);
                        mergeAdapter.addAdapter(swingBottomInAnimationAdapterApp);
//                        listView.setAdapter(swingBottomInAnimationAdapter);
                        appCardsAdapter.setData(places);
                        twitterSetup();
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
                    try{
                        Log.d("faceboook", response.toString());
                        GraphObject responseGraphObject = response.getGraphObject();
                        JSONObject json = responseGraphObject.getInnerJSONObject();
//                        Type type = new TypeToken<ArrayList<NewsFeedItem>>() {
//                        }.getType();
//                        ArrayList<NewsFeedItem> posts = gson.fromJson(json.getJSONArray("data").toString(), type);
                        ArrayList<NewsFeedItem> posts = new ArrayList<NewsFeedItem>();

                        JSONArray dataArray = (JSONArray)json.get("data");
                        GraphObjectList<NewsFeedItem> newsFeedItems = GraphObject.Factory.createList(dataArray, NewsFeedItem.class);
                        for (NewsFeedItem newsfeedItem : newsFeedItems) {
                            posts.add(newsfeedItem);
                        }

                        fbCardsAdapter = new FbCardsAdapter(getSherlockActivity(), posts);
                        swingBottomInAnimationAdapterFb = new SwingBottomInAnimationAdapter(fbCardsAdapter);
                        swingBottomInAnimationAdapterFb.setInitialDelayMillis(300);
                        swingBottomInAnimationAdapterFb.setAbsListView(listView);
                        mergeAdapter.addAdapter(swingBottomInAnimationAdapterFb);
//                        listView.setAdapter(swingBottomInAnimationAdapterFb);
                        fbCardsAdapter.setData(posts);
                        getAppNetFeed();

                    }catch (Exception e){
                        Log.d("facebook", e.toString());
                    }
                }
            };

            Bundle bundle = new Bundle();
            bundle.putString("fields", "id,from,name,message,caption,description,created_time,updated_time,type,status_type,via,source,picture, application");

            Request request = new Request(session, "me/home/", bundle, HttpMethod.GET, callback);
            request.executeAsync();

        }

    }

    public class TwitCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<Status> mFeed;
        private boolean mShouldReturnEmpty = true;

        public TwitCardsAdapter(Context context, List<Status> feed) {
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
                viewHolder.repostPost = (ImageView) view.findViewById(R.id.repost_post);
                viewHolder.starPost = (ImageView) view.findViewById(R.id.star_post);
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

            viewHolder.starPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo
                }
            });

            viewHolder.repostPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
//            setImageView(viewHolder, position);

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
            ImageView starPost;
            ImageView repostPost;
        }

        public String stripHtml(String html) {
            return Html.fromHtml(html).toString();
        }
    }

    public class AppCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Post> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public AppCardsAdapter(Context context, ArrayList<Post> appPlace) {
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

    public class FbCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<NewsFeedItem> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public FbCardsAdapter(Context context, ArrayList<NewsFeedItem> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(ArrayList<NewsFeedItem> appPlace){
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
        public NewsFeedItem getItem(int position) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.facebook_feed_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.social_checkin_name);
                viewHolder.userImg = (ImageView) view.findViewById(R.id.user_image);
                viewHolder.repostPost = (ImageView) view.findViewById(R.id.repost_post);
                viewHolder.likePost = (ImageView) view.findViewById(R.id.like_post);
                viewHolder.postTime = (RelativeTimeTextView) view.findViewById(R.id.post_time);
                viewHolder.postClient = (TextView) view.findViewById(R.id.post_info_client);
                viewHolder.postUser = (TextView) view.findViewById(R.id.post_info_user);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final NewsFeedItem post = getItem(position);
//            if(post.pictureMessage != null){
//                viewHolder.textView.setText(post.pictureMessage);
//            }else{
            viewHolder.textView.setText(post.getMessage());
//            }
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("DD-MM-YYYY");
                Date d = dateFormat.parse(post.getCreated_Time());
                viewHolder.postTime.setText(d.toString());
            }catch (Exception e){

            }

            String getCreatedTime = post.getCreated_Time();
//            long finalTimeStamp = Long.valueOf(getCreatedTime);
//            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");

            SimpleDateFormat formatter = getDateFormat();
            ParsePosition pos = new ParsePosition(0);
            long then = formatter.parse(getCreatedTime, pos).getTime();
            long now = new Date().getTime();

            long seconds = (now - then)/1000;
            long minutes = seconds/60;
            long hours = minutes/60;
            long days = hours/24;

            String friendly = null;
            long num = 0;
            if (days > 0) {
                num = days;
                friendly = days + " day";
            } else if (hours > 0) {
                num = hours;
                friendly = hours + " hour";
            } else if (minutes > 0) {
                num = minutes;
                friendly = minutes + " minute";
            } else {
                num = seconds;
                friendly = seconds + " second";
            }
            if (num > 1) {
                friendly += "s";
            }
            String postTimeStamp = friendly + " ago";
            viewHolder.postTime.setText(postTimeStamp);
//            viewHolder.postTime.setText(post.getCreated_Time().toString());
            //TODO add on click to these to open the respective client or user profile
            try{
                viewHolder.postClient.setText("via " + post.getApplication().getName());
            }catch (Exception e){
                e.printStackTrace();
                viewHolder.postClient.setText("via " + "Unavailable");
            }
            viewHolder.postUser.setText("from " + post.getFrom().getName());

            Picasso.with(mContext)
                    .load(Constants.FACEBOOK_GRAPH + post.getFrom().getID() + "/picture?type=large")
                    .resize(200, 200)
                    .centerCrop()
                    .into(viewHolder.userImg);

            viewHolder.likePost.setColorFilter(getResources().getColor(R.color.white));
            viewHolder.likePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Request likeRequest = new Request(Session.getActiveSession(), post.getID() + "/likes", null, HttpMethod.POST, new Request.Callback() {

                        @Override
                        public void onCompleted(Response response) {
                            Log.i("facebook", response.toString());
                        }
                    });
                    Request.executeBatchAndWait(likeRequest);

                }
            });

            viewHolder.repostPost.setColorFilter(getResources().getColor(R.color.white));
            viewHolder.repostPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            ImageView likePost;
            ImageView repostPost;
        }

        public SimpleDateFormat getDateFormat() {
            return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
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
