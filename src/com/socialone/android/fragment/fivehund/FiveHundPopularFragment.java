package com.socialone.android.fragment.fivehund;

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
import com.socialone.android.library.actionbarsherlock.PullToRefreshLayout;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.socialone.android.viewcomponents.SmoothProgressBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by david.hodge on 2/24/14.
 */
public class FiveHundPopularFragment extends SherlockFragment {

    View view;
    ListView listView;
    GoogleCardsAdapter googleCardsAdapter;
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
        try {
            setUpFeed();
        } catch (Exception e) {
            Log.d("fivehund", e.toString());
        }

//        ActionBarPullToRefresh.from(getSherlockActivity())
//                .allChildrenArePullable()
//                .listener(new OnRefreshListener() {
//                    @Override
//                    public void onRefreshStarted(View view) {
//                        try {
//                            setUpFeed();
//                        } catch (Exception e) {
//                            Log.d("fivehund", e.toString());
//                        }
//                    }
//                })
//                .setup(mPullToRefreshLayout);
    }

    private void setUpFeed() throws Exception {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("https://api.500px.com")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("consumer_key", "tDiwsrxjuhuFbadyMLNKKQxYmUtdrez1aUY2Itx8");
                    }
                })
                .setErrorHandler(new retrofit.ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        int statusCode = retrofitError.getResponse().getStatus();
                        if (retrofitError.isNetworkError()
                                || (500 <= statusCode && statusCode < 600)) {
                            return new Exception();
                        }
                        return retrofitError;
                    }
                })
                .build();

        FiveHundPopularService service = restAdapter.create(FiveHundPopularService.class);
        FiveHundPopularService.PhotosResponse response = service.getPopularPhotos();
        ArrayList<FiveHundPopularService.Photo> photos = response.photos;
//        photos = response.photos;

//        if (mPullToRefreshLayout != null && mPullToRefreshLayout.isRefreshing()) {
//            mPullToRefreshLayout.setRefreshComplete();
//        }

        googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), photos);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
        swingBottomInAnimationAdapter.setInitialDelayMillis(300);
        swingBottomInAnimationAdapter.setAbsListView(listView);
        listView.setAdapter(swingBottomInAnimationAdapter);
        googleCardsAdapter.setData(photos);
    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<FiveHundPopularService.Photo> mFeed;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, ArrayList<FiveHundPopularService.Photo> feed) {
            mContext = context;
            mFeed = feed;
        }

        public void setData(ArrayList<FiveHundPopularService.Photo> feed) {
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
        public FiveHundPopularService.Photo getItem(int position) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.flickr_feed_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.flickrName = (TextView) view.findViewById(R.id.flickr_name);
                viewHolder.flickrExtra = (RelativeTimeTextView) view.findViewById(R.id.flickr_extra);
                viewHolder.flickrImage = (ImageView) view.findViewById(R.id.flickr_imageview);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final FiveHundPopularService.Photo feed = getItem(position);
            viewHolder.flickrName.setText(feed.name);
            viewHolder.flickrExtra.setText(feed.user.fullname);

            Picasso.with(mContext)
                    .load(feed.image_url)
                    .into(viewHolder.flickrImage);

//            viewHolder.flickrImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent photoViewerIntent = new Intent(getSherlockActivity(), FlickrImageViewer.class);
//                    photoViewerIntent.putExtra("photoid", feed.getId());
//                    startActivity(photoViewerIntent);
//                }
//            });

            return view;
        }

        public class ViewHolder {
            TextView flickrName;
            RelativeTimeTextView flickrExtra;
            ImageView flickrImage;
        }

        public String stripHtml(String html) {
            return Html.fromHtml(html).toString();
        }
    }
}
