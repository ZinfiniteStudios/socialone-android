package com.socialone.android.fragment.tumblr;

import android.content.Context;
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

import com.actionbarsherlock.app.SherlockFragment;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.utils.Constants;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.squareup.picasso.Picasso;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by david.hodge on 1/22/14.
 */
public class TumblrMainFeedFragment extends SherlockFragment {

    View view;
    ListView listView;
    SharedPreferences prefs;
    JumblrClient client;
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
        prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        getUserDashboard();
    }

    private void getUserDashboard(){
        client = new JumblrClient(Constants.TUMBLR_CONSUMER_KEY, Constants.TUMBLR_CONSUMER_SECRET);
        client.setToken(prefs.getString(Constants.TUMBLR_ACCESS, null), prefs.getString(Constants.TUMBLR_SECRET, null));
        List<Post> posts = client.userDashboard();

        googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), posts);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
        swingBottomInAnimationAdapter.setInitialDelayMillis(300);
        swingBottomInAnimationAdapter.setAbsListView(listView);
        listView.setAdapter(swingBottomInAnimationAdapter);
        googleCardsAdapter.setData(posts);
    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<Post> mFeed;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, List<Post> feed) {
            mContext = context;
            mFeed = feed;
        }

        public void setData(List<Post> feed){
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
        public Post getItem(int position) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.instagram_feed_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.instaInfo = (TextView) view.findViewById(R.id.insta_info);
                viewHolder.userRealName = (TextView) view.findViewById(R.id.insta_name);
                viewHolder.userTwitName = (TextView) view.findViewById(R.id.insta_user_name);
                viewHolder.userImg = (ImageView) view.findViewById(R.id.insta_imageview);
                viewHolder.postTime = (RelativeTimeTextView) view.findViewById(R.id.insta_time);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final Post post = getItem(position);
            viewHolder.userRealName.setText(post.getBlogName());
            Log.d("tumblr", "post" + Integer.toString(position) + post.getBlogName() + " " + post.getFormat() + " " + post.getId() +  " " + post.getPostUrl() + " " + post.getRebloggedFromId() + " " + post.getReblogKey() +
            " " + post.getSourceUrl() + " " + post.getState() + " " + post.getTags().toArray().toString()  +  " " + post.getType() + " " + post.getTimestamp());

            viewHolder.postTime.setText(post.getDateGMT());
            viewHolder.instaInfo.setText(post.getSourceUrl());

            Picasso.with(mContext)
                    .load(post.getClient().blogAvatar(post.getBlogName()))
                    .into(viewHolder.userImg);


            return view;
        }

        public class ViewHolder {
            TextView instaInfo;
            TextView userRealName;
            TextView userTwitName;
            RelativeTimeTextView postTime;
            ImageView userImg;
        }

        public String stripHtml(String html) {
            return Html.fromHtml(html).toString();
        }

        public SimpleDateFormat getDateFormat() {
            return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        }
    }
}
