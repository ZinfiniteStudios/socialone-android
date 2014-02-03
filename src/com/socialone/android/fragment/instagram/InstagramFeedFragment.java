package com.socialone.android.fragment.instagram;

import android.content.Context;
import android.content.Intent;
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
import com.socialone.android.activity.InstagramImageViewer;
import com.socialone.android.jinstagram.Instagram;
import com.socialone.android.jinstagram.auth.model.Token;
import com.socialone.android.jinstagram.auth.oauth.InstagramService;
import com.socialone.android.jinstagram.entity.common.ImageData;
import com.socialone.android.jinstagram.entity.users.feed.MediaFeed;
import com.socialone.android.jinstagram.entity.users.feed.MediaFeedData;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by david.hodge on 1/19/14.
 */
public class InstagramFeedFragment extends SherlockFragment {


    View view;
    ListView listView;
    SocialAuthAdapter instaAuthAdapter;
    GoogleCardsAdapter googleCardsAdapter;
    InstagramService instagramService;
    Instagram instagram;
    private PullToRefreshLayout mPullToRefreshLayout;
    SmoothProgressBar emptyView;

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
        emptyView = (SmoothProgressBar) view.findViewById(R.id.empty_loader);
        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setEmptyView(emptyView);
        instaSetup();
        ActionBarPullToRefresh.from(getSherlockActivity())
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        getInstaFeed();
                    }
                })
                .setup(mPullToRefreshLayout);
    }



    private void instaSetup(){
        instaAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Token token = new Token(instaAuthAdapter.getCurrentProvider().getAccessGrant().getKey(), instaAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
                instagram = new Instagram(token);
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
        try{
            MediaFeed feed = instagram.getUserFeeds();
            List<MediaFeedData> userFeed = feed.getData();
            mPullToRefreshLayout.setRefreshComplete();
            googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), userFeed);
            SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
            swingBottomInAnimationAdapter.setInitialDelayMillis(300);
            swingBottomInAnimationAdapter.setAbsListView(listView);
            listView.setAdapter(swingBottomInAnimationAdapter);
            googleCardsAdapter.setData(userFeed);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<MediaFeedData> mFeed;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, List<MediaFeedData> feed) {
            mContext = context;
            mFeed = feed;
        }

        public void setData(List<MediaFeedData> feed){
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
        public MediaFeedData getItem(int position) {
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

            final MediaFeedData feed = getItem(position);
            viewHolder.userRealName.setText(feed.getUser().getFullName());
            viewHolder.userTwitName.setText(feed.getUser().getUserName());

            try{
            Long referenceTime = Long.parseLong(feed.getCaption().getCreatedTime());
            viewHolder.postTime.setReferenceTime(new Date(referenceTime * 1000).getTime());
            }catch (Exception e){
                Log.d("instagram", e.toString());
            }
            viewHolder.instaInfo.setText(Integer.toString(feed.getLikes().getCount()) + " likes");
            //TODO add on click to these to open the respective client or user profile
//            viewHolder.postClient.setText("via " + stripHtml(feed.getSource()));
//            viewHolder.postUser.setText("from " + feed.getUser().get);

            ImageData imageData = feed.getImages().getStandardResolution();
            Picasso.with(mContext)
                    .load(imageData.getImageUrl())
                    .into(viewHolder.userImg);

            viewHolder.userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoViewerIntent = new Intent(getSherlockActivity(), InstagramImageViewer.class);
                    photoViewerIntent.putExtra("photoid", feed.getId());
                    startActivity(photoViewerIntent);
                }
            });

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
