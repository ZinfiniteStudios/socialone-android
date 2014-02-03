package com.socialone.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.socialone.android.utils.Constants;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterListener;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by david.hodge on 2/1/14.
 */
public class TwitterPostService extends IntentService {

    ConfigurationBuilder cb;
    boolean addPhoto = false;
    boolean locationShare = false;
    String picturePath;
    SocialAuthAdapter mAuthAdapter;
    Location location;
    LocationManager locationManager;
    String shareText;

    public TwitterPostService(){
        super(TwitterPostService.class.getSimpleName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras!=null){
            shareText = extras.getString("share_text");
            addPhoto = extras.getBoolean("addPhoto");
            locationShare = extras.getBoolean("addLocation");
            if(addPhoto){
                picturePath = extras.getString("picturePath");
            }
        }
        twitterShare(shareText);
    }

    @Override
    public void onCreate(){

    }

    @Override
    public void onStart(Intent intent, int startId){

    }

    @Override
    public void onDestroy(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        if(extras!=null){
            shareText = extras.getString("share_text");
            addPhoto = extras.getBoolean("addPhoto");
            locationShare = extras.getBoolean("addLocation");
            if(addPhoto){
                picturePath = extras.getString("picturePath");
            }
        }
        twitterSetup();

        return super.onStartCommand(intent, flags, startId);
    }

    private void twitterSetup(){
        mAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Log.d("twitter", "auth adapter completed");
                twitterShare(shareText);
            }

            @Override
            public void onError(SocialAuthError socialAuthError) {
                Log.e("twitter", "auth adapter " + socialAuthError.getMessage());
                Log.e("twitter", "auth adapter " + socialAuthError.toString());
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
        mAuthAdapter.addCallBack(SocialAuthAdapter.Provider.TWITTER, Constants.TWITTER_CALLBACK);
        mAuthAdapter.authorize(this, SocialAuthAdapter.Provider.TWITTER);
    }

    private void twitterShare(String string){
        final String share = string;
        byte[] data = null;

        if(locationShare){
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            String bestProvider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(bestProvider);
        }

        if(addPhoto){
            try{
                Bitmap bi = BitmapFactory.decodeFile(picturePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bi.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                data = baos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(data);
                Log.d("twitter", share);
                cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey(Constants.TWIT_CONSUMER_KEY)
                        .setOAuthConsumerSecret(Constants.TWIT_CONSUMER_SECRET)
                        .setOAuthAccessToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey())
                        .setOAuthAccessTokenSecret(mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());

                TwitterListener listener = new TwitterAdapter() {
                    @Override public void updatedStatus(Status status) {
                        System.out.println("Successfully updated the status to [" +
                                status.getText() + "].");
                    }

//                    @Override
//                    public void onException(TwitterException e, int method) {
//                        e.printStackTrace();
//                    }
                };

                AsyncTwitterFactory factory = new AsyncTwitterFactory(cb.build());
                AsyncTwitter asyncTwitter = factory.getInstance();
                asyncTwitter.addListener(listener);

//                TwitterFactory tf = new TwitterFactory(cb.build());
//                Twitter twitter = tf.getInstance();
                StatusUpdate statusUpdate = new StatusUpdate(string);
                if(locationShare){
                    statusUpdate.setLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
                    statusUpdate.setDisplayCoordinates(true);
                }
                statusUpdate.setMedia("userimg.jpg", bs);
                asyncTwitter.updateStatus(statusUpdate);
//                Status status = twitter.updateStatus(statusUpdate);
                Toast.makeText(this, "Twitter Share Complete!", Toast.LENGTH_LONG).show();
                this.stopSelf();
            }catch (Exception e){
                Log.e("twitter", e.toString());
            }
        }else {

            try {

                Log.d("twitter", share);
                cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey(Constants.TWIT_CONSUMER_KEY)
                        .setOAuthConsumerSecret(Constants.TWIT_CONSUMER_SECRET)
                        .setOAuthAccessToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey())
                        .setOAuthAccessTokenSecret(mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());

                TwitterListener listener = new TwitterAdapter() {
                    @Override public void updatedStatus(Status status) {
                        System.out.println("Successfully updated the status to [" +
                                status.getText() + "].");
                    }

//                    @Override
//                    public void onException(TwitterException e, int method) {
//                        e.printStackTrace();
//                    }
                };

                AsyncTwitterFactory factory = new AsyncTwitterFactory(cb.build());
                AsyncTwitter asyncTwitter = factory.getInstance();
                asyncTwitter.addListener(listener);

//                TwitterFactory tf = new TwitterFactory(cb.build());
//                Twitter twitter = tf.getInstance();
                StatusUpdate statusUpdate = new StatusUpdate(string);
                if(locationShare){
                    statusUpdate.setLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
                    statusUpdate.setDisplayCoordinates(true);
                }
                asyncTwitter.updateStatus(statusUpdate);
//                Status status = twitter.updateStatus(statusUpdate);
                Toast.makeText(this, "Twitter Share Complete!", Toast.LENGTH_LONG).show();
                this.stopSelf();
            }catch (Exception e){
                Log.e("twitter", e.toString());
            }
        }

    }
}
