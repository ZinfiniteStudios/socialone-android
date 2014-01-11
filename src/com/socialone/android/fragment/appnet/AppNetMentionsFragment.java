package com.socialone.android.fragment.appnet;

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
import com.parse.signpost.OAuth;
import com.socialone.android.R;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.data.Post;
import com.socialone.android.appnet.adnlib.data.PostList;
import com.socialone.android.appnet.adnlib.response.PostListResponseHandler;
import com.socialone.android.appnet.adnlib.response.PostResponseHandler;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by david.hodge on 1/10/14.
 */
public class AppNetMentionsFragment extends SherlockFragment {

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
        client.retrievePostsMentioningCurrentUser(new PostListResponseHandler() {
            @Override
            public void onSuccess(PostList responseData) {
                final ArrayList<Post> places = responseData;
                getSherlockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
        private ArrayList<Post> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, ArrayList<Post> appPlace) {
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
}
