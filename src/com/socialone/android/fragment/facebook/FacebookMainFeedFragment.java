package com.socialone.android.fragment.facebook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
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
import com.socialone.android.R;
import com.socialone.android.api.facebook.NewsFeedItem;
import com.socialone.android.library.ActionBarPullToRefresh;
import com.socialone.android.library.actionbarsherlock.PullToRefreshLayout;
import com.socialone.android.library.listeners.OnRefreshListener;
import com.socialone.android.utils.Constants;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.socialone.android.viewcomponents.SmoothProgressBar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by david.hodge on 1/4/14.
 */
public class FacebookMainFeedFragment extends SherlockFragment {

    View view;
    ListView listView;
    GoogleCardsAdapter googleCardsAdapter;

    private PullToRefreshLayout mPullToRefreshLayout;
    private UiLifecycleHelper uiHelper;
    Session session;
    Gson gson;
    SmoothProgressBar emptyView;

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
        emptyView = (SmoothProgressBar) view.findViewById(R.id.empty_loader);
        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setEmptyView(emptyView);
        getFacebookFeed();

        ActionBarPullToRefresh.from(getSherlockActivity())
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        getFacebookFeed();
                    }
                })
                .setup(mPullToRefreshLayout);
    }

    private void getFacebookFeed() {
        session = ensureFacebookSessionFromCache(getSherlockActivity());

        if (session != null) {

            Request.Callback callback = new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    try {
                        Log.d("faceboook", response.toString());
                        GraphObject responseGraphObject = response.getGraphObject();
                        JSONObject json = responseGraphObject.getInnerJSONObject();
//                        Type type = new TypeToken<ArrayList<NewsFeedItem>>() {
//                        }.getType();
//                        ArrayList<NewsFeedItem> posts = gson.fromJson(json.getJSONArray("data").toString(), type);
                        ArrayList<NewsFeedItem> posts = new ArrayList<NewsFeedItem>();

                        JSONArray dataArray = (JSONArray) json.get("data");
                        GraphObjectList<NewsFeedItem> newsFeedItems = GraphObject.Factory.createList(dataArray, NewsFeedItem.class);
                        for (NewsFeedItem newsfeedItem : newsFeedItems) {
                            posts.add(newsfeedItem);
                        }
                        mPullToRefreshLayout.setRefreshComplete();
                        googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), posts);
                        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
                        swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                        swingBottomInAnimationAdapter.setAbsListView(listView);
                        listView.setAdapter(swingBottomInAnimationAdapter);
                        googleCardsAdapter.setData(posts);

                    } catch (Exception e) {
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


    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<NewsFeedItem> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, ArrayList<NewsFeedItem> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(ArrayList<NewsFeedItem> appPlace) {
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

        public void addRangeToTop(ArrayList<NewsFeedItem> items) {

            for (int i = 0; i < items.size(); i++) {
                mAppPlace.add(0, items.get(i));
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.facebook_feed_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.postType = (TextView) view.findViewById(R.id.social_post_type);
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
            viewHolder.postType.setText(post.getType() + " by " + post.getFrom().getName());
            if(!TextUtils.isEmpty(post.getMessage())){
               viewHolder.textView.setText(post.getMessage());
            }else{
                viewHolder.textView.setText(post.getDescription());

            }
            Linkify.addLinks(viewHolder.textView, Linkify.ALL);
            viewHolder.textView.setLinkTextColor(getResources().getColor(R.color.custom_blue));
//            }
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("DD-MM-YYYY");
                Date d = dateFormat.parse(post.getCreated_Time());
                viewHolder.postTime.setText(d.toString());
            } catch (Exception e) {

            }

            String getCreatedTime = post.getCreated_Time();

            SimpleDateFormat formatter = getDateFormat();
            ParsePosition pos = new ParsePosition(0);
            long then = formatter.parse(getCreatedTime, pos).getTime();
            long now = new Date().getTime();

            long seconds = (now - then) / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

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
            try {
                viewHolder.postClient.setText("via " + post.getApplication().getName());
            } catch (Exception e) {
                e.printStackTrace();
//                viewHolder.postClient.setText("via " + post.getVia().getName());
            }
//            viewHolder.postUser.setText("from " + post.getFrom().getName());

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

            return view;
        }

        public class ViewHolder {
            TextView postType;
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
            Toast.makeText(getSherlockActivity(), "error", Toast.LENGTH_SHORT).show();
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

    public static Session ensureFacebookSessionFromCache(Context context) {
        Session activeSession = Session.getActiveSession();
        if (activeSession == null || !activeSession.getState().isOpened()) {
            activeSession = Session.openActiveSessionFromCache(context);
        }
        return activeSession;
    }
}
