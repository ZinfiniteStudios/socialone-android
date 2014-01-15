package com.socialone.android.fragment.appnet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
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
import com.socialone.android.appnet.adnlib.data.User;
import com.socialone.android.appnet.adnlib.data.UserList;
import com.socialone.android.appnet.adnlib.response.UserListResponseHandler;
import com.socialone.android.utils.BlurTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by david.hodge on 1/15/14.
 */
public class AppNetFollowingFragment extends SherlockFragment {

    View view;
    ListView listView;
    AppDotNetClient client;
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
        getAppNetFeed();
    }

    private void getAppNetFeed(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        client = new AppDotNetClient(prefs.getString(OAuth.OAUTH_TOKEN, null));

        client.retrieveFollowedUsers("me", new UserListResponseHandler() {
            @Override
            public void onSuccess(final UserList responseData) {
                final ArrayList<User> users = responseData;
                getSherlockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), users);
                        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(googleCardsAdapter);
                        swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                        swingBottomInAnimationAdapter.setAbsListView(listView);
                        listView.setAdapter(swingBottomInAnimationAdapter);
                        googleCardsAdapter.setData(users);
                    }
                });
            }
        });

    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<User> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, ArrayList<User> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(ArrayList<User> appPlace){
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
        public User getItem(int position) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.friends_list_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.userBackground = (ImageView) view.findViewById(R.id.user_background_image);
                viewHolder.userProfile = (ImageView) view.findViewById(R.id.user_profile_image);
                viewHolder.userRealName = (TextView) view.findViewById(R.id.user_real_name);
                viewHolder.userUserName = (TextView) view.findViewById(R.id.user_user_name);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final User post = getItem(position);

            Picasso.with(mContext)
                    .load(post.getAvatarImage().getUrl())
                    .resize(100, 100)
                    .centerCrop()
                    .into(viewHolder.userProfile);

            Picasso.with(mContext)
                    .load(post.getCoverImage().getUrl())
                    .resize(750, 750)
                    .centerCrop()
                    .transform(new BlurTransformation(mContext))
                    .into(viewHolder.userBackground);

            viewHolder.userUserName.setText("@" + post.getUsername());
            viewHolder.userRealName.setText(post.getName());

            return view;
        }

        public class ViewHolder {
            ImageView userBackground;
            ImageView userProfile;
            TextView userRealName;
            TextView userUserName;
        }

        public String stripHtml(String html) {
            return Html.fromHtml(html).toString();
        }
    }
}
