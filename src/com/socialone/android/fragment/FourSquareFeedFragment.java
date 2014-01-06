package com.socialone.android.fragment;

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
import com.socialone.android.condesales.EasyFoursquareAsync;
import com.socialone.android.condesales.listeners.AccessTokenRequestListener;
import com.socialone.android.condesales.listeners.GetCheckInsListener;
import com.socialone.android.condesales.models.Checkin;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by david.hodge on 1/6/14.
 */
public class FourSquareFeedFragment extends SherlockFragment {

    View view;
    ListView listView;
    EasyFoursquareAsync easyFoursquareAsync;
    GoogleCardsAdapter googleCardsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        getFourSquareFeed();
    }

    private void getFourSquareFeed(){
        easyFoursquareAsync = new EasyFoursquareAsync(getSherlockActivity());
        easyFoursquareAsync.requestAccess(new AccessTokenRequestListener() {
            @Override
            public void onAccessGrant(String accessToken) {
                easyFoursquareAsync.getCheckIns(new GetCheckInsListener() {
                    @Override
                    public void onGotCheckIns(ArrayList<Checkin> list) {
                        final ArrayList<Checkin> venueArrayList = list;
                        Log.d("foursquare", venueArrayList.toArray().toString());
                        getSherlockActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), venueArrayList);
                                SwingBottomInAnimationAdapter swingBottomInAnimationAdapter =  new SwingBottomInAnimationAdapter(googleCardsAdapter);
                                swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                                swingBottomInAnimationAdapter.setAbsListView(listView);
                                listView.setAdapter(swingBottomInAnimationAdapter);
                                googleCardsAdapter.setData(venueArrayList);
                            }
                        });

                    }

                    @Override
                    public void onError(String errorMsg) {

                    }
                });
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Checkin> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, ArrayList<Checkin> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(ArrayList<Checkin> appPlace){
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
        public Checkin getItem(int position) {
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

            final Checkin post = getItem(position);
            viewHolder.textView.setText(post.getShout());
            viewHolder.postTime.setText(Long.toString(post.getCreatedAt()));
            //TODO add on click to these to open the respective client or user profile
            viewHolder.postClient.setText("via " + post.getType());
            try{
                viewHolder.postUser.setText("from " + post.getUser().getFirstName());
            }catch (Exception e){
                e.printStackTrace();
                viewHolder.postUser.setText("from " + "Unknown");
            }

            Picasso.with(mContext)
                    .load(post.getVenue().getCanonicalUrl())
                    .resize(200, 200)
                    .centerCrop()
                    .into(viewHolder.userImg);

            viewHolder.starPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

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
            ImageView starPost;
            ImageView repostPost;
        }

        public String stripHtml(String html) {
            return Html.fromHtml(html).toString();
        }
    }
}
