package com.socialone.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.Size;
import com.socialone.android.R;
import com.socialone.android.utils.Constants;
import com.socialone.android.utils.FlickrHelper;
import com.socialone.android.utils.SystemBarTintManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by david.hodge on 1/17/14.
 */
public class FlickrImageViewer extends SherlockFragmentActivity {

    ImageView mainImage;
    TextView imageTitle;
    PhotoViewAttacher photoViewAttacher;
    SocialAuthAdapter mAuthAdapter;
    Flickr f;
    String photoId;
    private SystemBarTintManager systemTintBarUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.FlickrTheme);
        setContentView(R.layout.flickr_image_viewer);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
        mAuthAdapter.authorize(FlickrImageViewer.this, SocialAuthAdapter.Provider.FLICKR);
    }

    private void setUpFeed() throws Exception{
        Photo photo = f.getPhotosInterface().getPhoto(photoId);
        String urlToFetch = getLargestUrlAvailable(photo);
        Picasso.with(FlickrImageViewer.this)
                .load(urlToFetch)
                .into(mainImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        photoViewAttacher.update();
                    }

                    @Override
                    public void onError() {

                    }
                });
        imageTitle.setText(photo.getTitle());
    }

    private String getLargestUrlAvailable(Photo photo) {
        String url = "";
        Size size = photo.getLargeSize();
        if (size != null) {
            url = photo.getLargeUrl();
        } else {
            /* No large size available, fall back to medium */
            url = photo.getMediumUrl();
        }
        return url;
    }
}
