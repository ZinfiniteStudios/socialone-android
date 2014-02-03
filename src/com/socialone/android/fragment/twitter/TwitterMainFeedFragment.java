package com.socialone.android.fragment.twitter;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.library.ActionBarPullToRefresh;
import com.socialone.android.library.actionbarsherlock.PullToRefreshLayout;
import com.socialone.android.library.listeners.OnRefreshListener;
import com.socialone.android.utils.Constants;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.socialone.android.viewcomponents.SmoothProgressBar;
import com.squareup.picasso.Picasso;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.List;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by david.hodge on 1/3/14.
 */
public class TwitterMainFeedFragment extends SherlockFragment {


    View view;
    ListView listView;
    GoogleCardsAdapter googleCardsAdapter;
    SocialAuthAdapter mAuthAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;
    SmoothProgressBar emptyView;
    List<Status> feed;
    ActionMode.Callback modeCallBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        modeCallBack = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.social_share_menu, menu);
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        twitterSetup();
        ActionBarPullToRefresh.from(getSherlockActivity())
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        setUpTwit4j();
                    }
                })
                .setup(mPullToRefreshLayout);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getSherlockActivity().startActionMode(modeCallBack);
                Log.d("twitter", "long item click");
                return true;
            }
        });
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.setTitle("Options");
        mode.getMenuInflater().inflate(R.menu.social_share_menu, menu);
        return true;
    }

    private void setUpTwit4j() {
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(Constants.TWIT_CONSUMER_KEY)
                    .setOAuthConsumerSecret(Constants.TWIT_CONSUMER_SECRET)
                    .setOAuthAccessToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey())
                    .setOAuthAccessTokenSecret(mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());

            TwitterListener twitterListener = new TwitterAdapter() {

                @Override
                public void onException(TwitterException te, TwitterMethod method) {
                    super.onException(te, method);
                    Log.d("twitterfeed", te.toString());
                }

                @Override
                public void gotHomeTimeline(final ResponseList<Status> statuses) {
                    super.gotHomeTimeline(statuses);
                    getSherlockActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            feed = statuses;
                            googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), feed);
                            SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
                            swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                            swingBottomInAnimationAdapter.setAbsListView(listView);
                            listView.setAdapter(swingBottomInAnimationAdapter);
                            googleCardsAdapter.notifyDataSetChanged();
                            mPullToRefreshLayout.setRefreshComplete();
                        }
                    });
                }
            };

            AsyncTwitterFactory factory = new AsyncTwitterFactory(cb.build());
            AsyncTwitter asyncTwitter = factory.getInstance();
            asyncTwitter.addListener(twitterListener);
            asyncTwitter.getHomeTimeline();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void twitterFeedRefresh() {
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(Constants.TWIT_CONSUMER_KEY)
                    .setOAuthConsumerSecret(Constants.TWIT_CONSUMER_SECRET)
                    .setOAuthAccessToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey())
                    .setOAuthAccessTokenSecret(mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            List<Status> statuses = twitter.getHomeTimeline();
            googleCardsAdapter.addRangeToTop(statuses);
            googleCardsAdapter.notifyDataSetChanged();
            mPullToRefreshLayout.setRefreshComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void twitterSetup() {
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

        public void setData(List<Status> feed) {
            mFeed = feed;
        }

        public void addRangeToTop(List<Status> feed) {

            for (int i = 0; i < feed.size(); i++) {
                mFeed.add(0, feed.get(i));
            }
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

//        public void AddRangeToTop(ArrayList<Status> feed) {
//            for (Status mFeed : feed){
//                this.insert(feed, 0);
//            }
//        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
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
            Linkify.addLinks(viewHolder.textView, Linkify.ALL);
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

            viewHolder.userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getSherlockActivity(), feed.getUser().getProfileBackgroundColor().toString(), Toast.LENGTH_LONG).show();
                }
            });


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO tweet details
                }
            });

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
}
