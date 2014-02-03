package com.socialone.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.socialone.android.R;
import com.socialone.android.jinstagram.Instagram;
import com.socialone.android.jinstagram.auth.model.Token;
import com.socialone.android.jinstagram.entity.media.MediaInfoFeed;
import com.socialone.android.utils.Constants;
import com.socialone.android.utils.SystemBarTintManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by david.hodge on 1/31/14.
 */
public class InstagramImageViewer extends SherlockActivity {

    ImageView mainImage;
    TextView imageTitle;
    PhotoViewAttacher photoViewAttacher;
    private SystemBarTintManager systemTintBarUtil;
    String photoId;
    SocialAuthAdapter instaAuthAdapter;
    Instagram instagram;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.FlickrTheme);
        setContentView(R.layout.flickr_image_viewer);
        mContext = this;

        mainImage = (ImageView) findViewById(R.id.main_image_view);
        imageTitle = (TextView) findViewById(R.id.image_title);
        photoViewAttacher = new PhotoViewAttacher(mainImage);

        Intent intent = getIntent();
        photoId = intent.getStringExtra("photoid");

        systemTintBarUtil = new SystemBarTintManager(this);
        systemTintBarUtil.setStatusBarTintEnabled(true);
        systemTintBarUtil.setNavigationBarTintEnabled(true);
        systemTintBarUtil.setStatusBarTintColor(getResources().getColor(R.color.translucent_black));
        systemTintBarUtil.setNavigationBarTintColor(getResources().getColor(R.color.translucent_black));

        instaSetup();
    }

    private void instaSetup(){
        instaAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Token token = new Token(instaAuthAdapter.getCurrentProvider().getAccessGrant().getKey(), instaAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
                instagram = new Instagram(token);
                getInstaDetails();
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
        instaAuthAdapter.authorize(mContext, SocialAuthAdapter.Provider.INSTAGRAM);
    }

    private void getInstaDetails(){
        try{
            MediaInfoFeed mediaInfoFeed = instagram.getMediaInfo(photoId);
            Picasso.with(mContext)
                    .load(mediaInfoFeed.getData().getImages().getStandardResolution().getImageUrl())
                    .into(mainImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            photoViewAttacher.update();
                        }

                        @Override
                        public void onError() {

                        }
                    });
            imageTitle.setText(mediaInfoFeed.getData().getUser().getUserName());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
