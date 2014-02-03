package com.socialone.android.fragment.googeplus;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
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

import org.brickred.socialauth.Feed;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import java.util.List;

/**
 * Created by david.hodge on 1/19/14.
 */
public class GooglePlusFeedFragment extends SherlockFragment {

    View view;
    ListView listView;
    SocialAuthAdapter plusAuthAdapter;
    GoogleCardsAdapter googleCardsAdapter;

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
        plusSetup();
    }



    private void plusSetup(){
        plusAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                getPlusFeed();
            }

            @Override
            public void onError(SocialAuthError socialAuthError) {
                Log.d("googleplus", socialAuthError.toString());
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
        plusAuthAdapter.addCallBack(SocialAuthAdapter.Provider.GOOGLEPLUS, Constants.PLUS_CALLBACK);
        plusAuthAdapter.authorize(getSherlockActivity(), SocialAuthAdapter.Provider.GOOGLEPLUS);
    }

    private void getPlusFeed(){
        plusAuthAdapter.getFeedsAsync(new SocialAuthListener<List<Feed>>() {
            @Override
            public void onExecute(String s, List<Feed> feeds) {
                Log.d("plus", s);
                Log.d("plus", feeds.toString());
                googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), feeds);
                SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
                swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                swingBottomInAnimationAdapter.setAbsListView(listView);
                listView.setAdapter(swingBottomInAnimationAdapter);
                googleCardsAdapter.setData(feeds);
            }

            @Override
            public void onError(SocialAuthError socialAuthError) {

            }
        });
    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<Feed> mFeed;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, List<Feed> feed) {
            mContext = context;
            mFeed = feed;
        }

        public void setData(List<Feed> feed){
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
        public Feed getItem(int position) {
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

            final Feed feed = getItem(position);
            viewHolder.textView.setText(feed.getMessage());
            viewHolder.userRealName.setText(feed.getFrom());
            viewHolder.userTwitName.setText(feed.getScreenName());
            viewHolder.postTime.setReferenceTime(feed.getCreatedAt().getTime());
            //TODO add on click to these to open the respective client or user profile
//            viewHolder.postClient.setText("via " + stripHtml(feed.getSource()));
//            viewHolder.postUser.setText("from " + feed.getUser().get);

            Picasso.with(mContext)
                    .load(feed.getId())
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
