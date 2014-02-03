package com.socialone.android.fragment.twitter;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.utils.Constants;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.ArrayList;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by david.hodge on 1/13/14.
 */
public class TwitterFeedStreamFragment extends SherlockFragment {

    View view;
    ListView listView;
    GoogleCardsAdapter googleCardsAdapter;
    SocialAuthAdapter mAuthAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.social_checkin_list, container, false);
        listView = (ListView) view.findViewById(R.id.activity_googlecards_listview);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        twitterSetup();
    }


    private void setUpTwitStream() {
        try {
            Log.d("twitterstream", "setting stream up");
            final List<Status> statuses = new ArrayList<Status>();
            googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), statuses);
            SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
            swingBottomInAnimationAdapter.setInitialDelayMillis(300);
            swingBottomInAnimationAdapter.setAbsListView(listView);
            listView.setAdapter(swingBottomInAnimationAdapter);
            googleCardsAdapter.setData(statuses);

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(Constants.TWIT_CONSUMER_KEY)
                    .setOAuthConsumerSecret(Constants.TWIT_CONSUMER_SECRET)
                    .setOAuthAccessToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey())
                    .setOAuthAccessTokenSecret(mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
            TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());
            TwitterStream twitterStream = twitterStreamFactory.getInstance();
            Log.d("twitterstream", "got twitter stream instance");
            UserStreamListener userStreamListener = new UserStreamListener() {
                @Override
                public void onDeletionNotice(long l, long l2) {

                }

                @Override
                public void onFriendList(long[] longs) {

                }

                @Override
                public void onFavorite(User user, User user2, Status status) {

                }

                @Override
                public void onUnfavorite(User user, User user2, Status status) {

                }

                @Override
                public void onFollow(User user, User user2) {

                }

                @Override
                public void onDirectMessage(DirectMessage directMessage) {

                }

                @Override
                public void onUserListMemberAddition(User user, User user2, UserList userList) {

                }

                @Override
                public void onUserListMemberDeletion(User user, User user2, UserList userList) {

                }

                @Override
                public void onUserListSubscription(User user, User user2, UserList userList) {

                }

                @Override
                public void onUserListUnsubscription(User user, User user2, UserList userList) {

                }

                @Override
                public void onUserListCreation(User user, UserList userList) {

                }

                @Override
                public void onUserListUpdate(User user, UserList userList) {

                }

                @Override
                public void onUserListDeletion(User user, UserList userList) {

                }

                @Override
                public void onUserProfileUpdate(User user) {

                }

                @Override
                public void onBlock(User user, User user2) {

                }

                @Override
                public void onUnblock(User user, User user2) {

                }

                @Override
                public void onStatus(Status status) {
                    statuses.add(0, status);
                    googleCardsAdapter.setData(statuses);
                    googleCardsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

                }

                @Override
                public void onTrackLimitationNotice(int i) {

                }

                @Override
                public void onScrubGeo(long l, long l2) {

                }

                @Override
                public void onStallWarning(StallWarning stallWarning) {
                    Log.d("twitterstream", stallWarning.getMessage());
                }

                @Override
                public void onException(Exception e) {
                    Log.d("twitterstream", e.toString());
                }
            };
            twitterStream.addListener(userStreamListener);
            twitterStream.user();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void twitterSetup() {
        mAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                setUpTwitStream();
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

        public void addRangeToTop(ArrayList<Status> feed) {

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
