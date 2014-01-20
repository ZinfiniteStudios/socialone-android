package com.socialone.android.fragment.instagram;

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
public class InstagramFeedFragment extends SherlockFragment {


    View view;
    ListView listView;
    SocialAuthAdapter instaAuthAdapter;
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
        instaSetup();
    }



    private void instaSetup(){
        instaAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                getInstaFeed();
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
        instaAuthAdapter.addCallBack(SocialAuthAdapter.Provider.INSTAGRAM, Constants.INSTAGRAM_CALLBACK);
        instaAuthAdapter.authorize(getSherlockActivity(), SocialAuthAdapter.Provider.INSTAGRAM);
    }

    private void getInstaFeed(){
        instaAuthAdapter.getFeedsAsync(new SocialAuthListener<List<Feed>>() {
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

            final Feed feed = getItem(position);
            viewHolder.userRealName.setText(feed.getFrom());
            viewHolder.userTwitName.setText(feed.getScreenName());
            viewHolder.postTime.setReferenceTime(feed.getCreatedAt().getTime());
            viewHolder.instaInfo.setText(feed.getId());
            //TODO add on click to these to open the respective client or user profile
//            viewHolder.postClient.setText("via " + stripHtml(feed.getSource()));
//            viewHolder.postUser.setText("from " + feed.getUser().get);

            Picasso.with(mContext)
                    .load(feed.getMessage())
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
    }
}
