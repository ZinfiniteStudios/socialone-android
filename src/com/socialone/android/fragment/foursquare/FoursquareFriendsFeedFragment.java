package com.socialone.android.fragment.foursquare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import com.socialone.android.condesales.criterias.TipsCriteria;
import com.socialone.android.condesales.listeners.AccessTokenRequestListener;
import com.socialone.android.condesales.listeners.FriendsCheckInListener;
import com.socialone.android.condesales.models.Checkin;
import com.socialone.android.library.ActionBarPullToRefresh;
import com.socialone.android.library.actionbarsherlock.PullToRefreshLayout;
import com.socialone.android.library.listeners.OnRefreshListener;
import com.socialone.android.utils.RoundCornerTransformation;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.socialone.android.viewcomponents.SmoothProgressBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by david.hodge on 1/27/14.
 */
public class FoursquareFriendsFeedFragment extends SherlockFragment {

    View view;
    ListView listView;
    EasyFoursquareAsync easyFoursquareAsync;
    GoogleCardsAdapter googleCardsAdapter;
    Location location;
    LocationManager locationManager;
    SmoothProgressBar emptyView;
    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        getFourSquareFeed();
        ActionBarPullToRefresh.from(getSherlockActivity())
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        getFourSquareFeed();
                    }
                })
                .setup(mPullToRefreshLayout);
    }

    private void getFourSquareFeed() {
        locationManager = (LocationManager) getSherlockActivity().getSystemService(getSherlockActivity().LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
        location = locationManager.getLastKnownLocation(bestProvider);

        easyFoursquareAsync = new EasyFoursquareAsync(getSherlockActivity());
        easyFoursquareAsync.requestAccess(new AccessTokenRequestListener() {
            @Override
            public void onAccessGrant(String accessToken) {
                TipsCriteria tipsCriteria = new TipsCriteria();
                tipsCriteria.setLocation(location);
                tipsCriteria.setQuantity(50);

                easyFoursquareAsync.getFriendsCheckIns(new FriendsCheckInListener() {
                    @Override
                    public void onGotCheckIns(ArrayList<Checkin> list) {
                        Log.d("foursquare", list.toString());
                        mPullToRefreshLayout.setRefreshComplete();
                        googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), list);
                        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
                        swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                        swingBottomInAnimationAdapter.setAbsListView(listView);
                        listView.setAdapter(swingBottomInAnimationAdapter);
                        googleCardsAdapter.setData(list);
                    }

                    @Override
                    public void onError(String errorMsg) {

                    }
                }, tipsCriteria);

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

        public void setData(ArrayList<Checkin> appPlace) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.four_square_checkin_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.social_checkin_name);
                viewHolder.userImg = (ImageView) view.findViewById(R.id.user_image);
                viewHolder.postTime = (RelativeTimeTextView) view.findViewById(R.id.post_time);
                viewHolder.postClient = (TextView) view.findViewById(R.id.post_info_client);
                viewHolder.postInfo = (TextView) view.findViewById(R.id.post_info);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final Checkin post = getItem(position);
            viewHolder.textView.setText(post.getUser().getFirstName() + " @ " + post.getVenue().getName());
            viewHolder.postTime.setReferenceTime(new Date(post.getCreatedAt() * 1000).getTime());
            //TODO add on click to these to open the respective client or user profile
            try {
                viewHolder.postClient.setText("via " + post.getSource().getName());
                viewHolder.postClient.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(post.getSource().getUrl())));
                    }
                });
            }catch (Exception e){

            }

            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
            viewHolder.postTime.setTypeface(tf);
            viewHolder.postInfo.setText(post.getShout());
            Picasso.with(mContext)
                    .load(post.getUser().getPhoto())
                    .transform(new RoundCornerTransformation(10,10))
                    .resize(200, 200)
                    .centerCrop()
                    .into(viewHolder.userImg);

            return view;
        }

        public class ViewHolder {
            TextView textView;
            RelativeTimeTextView postTime;
            TextView postClient;
            ImageView userImg;
            TextView postInfo;
        }

        public String stripHtml(String html) {
            return Html.fromHtml(html).toString();
        }
    }

}
