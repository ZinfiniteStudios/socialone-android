package com.socialone.android.fragment.appnet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.util.Linkify;
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
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.data.Interaction;
import com.socialone.android.appnet.adnlib.data.InteractionList;
import com.socialone.android.appnet.adnlib.response.InteractionListResponseHandler;
import com.socialone.android.viewcomponents.RelativeTimeTextView;

import java.util.ArrayList;

import oauth.signpost.OAuth;

/**
 * Created by david.hodge on 1/10/14.
 */
public class AppNetInteractionsFragment extends SherlockFragment {

    View view;
    ListView listView;
    AppDotNetClient client;
    GoogleCardsAdapter googleCardsAdapter;
//    PullToRefreshLayout pullToRefreshLayout;

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

//        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
//        ActionBarPullToRefresh.from(getActivity())
//                // Mark All Children as pullable
//                .allChildrenArePullable()
//                        // Set the OnRefreshListener
//                .listener(AppNetFeedFragment.this)
//                        // Finally commit the setup to our PullToRefreshLayout
//                .setup(pullToRefreshLayout);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAppNetFeed();
    }

    private void getAppNetFeed(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        client = new AppDotNetClient(prefs.getString(OAuth.OAUTH_TOKEN, null));
        client.retrieveCurrentUserInteractions(new InteractionListResponseHandler() {
            @Override
            public void onSuccess(InteractionList responseData) {
                final ArrayList<Interaction> places = responseData;
                getSherlockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
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

//    @Override
//    public void onRefreshStarted(View view) {
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                getAppNetFeed();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void result) {
//                super.onPostExecute(result);
//                // Notify PullToRefreshLayout that the refresh has finished
//                pullToRefreshLayout.setRefreshComplete();
//            }
//        }.execute();
//
//    }


    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Interaction> mFeed;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, ArrayList<Interaction> feed) {
            mContext = context;
            mFeed = feed;
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
            Linkify.addLinks(viewHolder.textView, Linkify.ALL);
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
}
