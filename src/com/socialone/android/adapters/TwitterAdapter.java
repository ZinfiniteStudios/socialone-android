package com.socialone.android.adapters;

import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.socialone.android.R;
import com.socialone.android.viewcomponents.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import twitter4j.Status;

/**
 * Created by david.hodge on 2/2/14.
 */
public class TwitterAdapter extends BaseAdapter {

    private Context mContext;
    private List<Status> mFeed;
    private boolean mShouldReturnEmpty = true;

    public TwitterAdapter(Context context, List<Status> feed) {
        mContext = context;
        mFeed = feed;
    }

    public void setData(List<Status> feed) {
        mFeed = feed;
    }

    public void addRangeToTop(List<Status> feed) {

        for (int i = 0; i < feed.size(); i++) {
            mFeed.add(0, feed.get(i));
        }
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
    public Status getItem(int position) {
        return mFeed.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

//        public void AddRangeToTop(ArrayList<Status> feed) {
//            for (Status mFeed : feed){
//                this.insert(feed, 0);
//            }
//        }

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

        final Status feed = getItem(position);
        viewHolder.textView.setText(feed.getText());
        Linkify.addLinks(viewHolder.textView, Linkify.ALL);
        viewHolder.userRealName.setText(feed.getUser().getName());
        viewHolder.userTwitName.setText("@" + feed.getUser().getScreenName());
        viewHolder.postTime.setReferenceTime(feed.getCreatedAt().getTime());
        //TODO add on click to these to open the respective client or user profile
        viewHolder.postClient.setText("via " + stripHtml(feed.getSource()));
//            viewHolder.postUser.setText("from " + feed.getUser().get);

        Picasso.with(mContext)
                .load(feed.getUser().getProfileImageURL())
                .resize(200, 200)
                .centerCrop()
                .into(viewHolder.userImg);

        viewHolder.userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, feed.getUser().getProfileBackgroundColor().toString(), Toast.LENGTH_LONG).show();
            }
        });

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
