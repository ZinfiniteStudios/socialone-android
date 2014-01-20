package com.socialone.android.fragment.flickr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;
import com.socialone.android.activity.FlickrImageViewer;
import com.socialone.android.utils.Constants;
import com.socialone.android.utils.FlickrHelper;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.Date;
import java.util.List;

/**
 * Created by david.hodge on 1/10/14.
 */
public class FlickrContactsFragment extends SherlockFragment{


    View view;
    ListView listView;
    GoogleCardsAdapter googleCardsAdapter;
    SocialAuthAdapter mAuthAdapter;
    Flickr f;
    OAuth auth;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
        flickrSetup();
    }

    private void flickrSetup(){
        mAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Log.d("flickr", "flickr complete");
                f = FlickrHelper.getInstance().getFlickrAuthed(
                        mAuthAdapter.getCurrentProvider().getAccessGrant().getKey(),
                        mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
                Log.d("flickr", "flickr helper complete");
//                f = new Flickr();
//                auth = new OAuth();
//                auth.setToken(new OAuthToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey(), mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret()));
                try{
                    Log.d("flickr", "setting feed");
                    setUpFeed();
                }catch (Exception e){
                    Log.d("flickr", e.toString());
                }
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
        mAuthAdapter.addCallBack(SocialAuthAdapter.Provider.FLICKR, Constants.FLICKR_CALLBACK);
        mAuthAdapter.authorize(getSherlockActivity(), SocialAuthAdapter.Provider.FLICKR);
    }

    private void setUpFeed() throws Exception{
        user = new User();
        String userName = user.getRealName();
//        Log.d("flickr", userName);
        Date day = null;
//        List<Photo> photos = f.getInterestingnessInterface().getList(day, Constants.EXTRAS, Constants.FETCH_PER_PAGE, 1);
        List<Photo> photos = f.getPhotosInterface().getContactsPhotos(50, false, false, true);
        googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), photos);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
        swingBottomInAnimationAdapter.setInitialDelayMillis(300);
        swingBottomInAnimationAdapter.setAbsListView(listView);
        listView.setAdapter(swingBottomInAnimationAdapter);
        googleCardsAdapter.setData(photos);
    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<Photo> mFeed;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, List<Photo> feed) {
            mContext = context;
            mFeed = feed;
        }

        public void setData(List<Photo> feed){
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
        public Photo getItem(int position) {
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

            final Photo feed = getItem(position);
            viewHolder.flickrName.setText(feed.getTitle());
            viewHolder.flickrExtra.setText(Integer.toString(feed.getViews()));
//            viewHolder.flickrExtra.setReferenceTime(feed.getDateAdded().getTime());
            try{
                Log.d("flickr", Integer.toString(feed.getViews()));
            }catch (Exception e){
                Log.d("flickr", e.toString());
            }
//            viewHolder.flickrExtra.setText(feed.getId());
            Picasso.with(mContext)
                    .load(feed.getLargeUrl())
                    .into(viewHolder.flickrImage);

            viewHolder.flickrImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoViewerIntent = new Intent(getSherlockActivity(), FlickrImageViewer.class);
                    photoViewerIntent.putExtra("photoid", feed.getId());
                    startActivity(photoViewerIntent);
                }
            });

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
