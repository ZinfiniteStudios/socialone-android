package com.socialone.android.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.plus.PlusShare;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.jabistudio.androidjhlabs.filter.ChannelMixFilter;
import com.jabistudio.androidjhlabs.filter.ContrastFilter;
import com.jabistudio.androidjhlabs.filter.DespeckleFilter;
import com.jabistudio.androidjhlabs.filter.GainFilter;
import com.jabistudio.androidjhlabs.filter.GrayscaleFilter;
import com.jabistudio.androidjhlabs.filter.KaleidoscopeFilter;
import com.jabistudio.androidjhlabs.filter.SharpenFilter;
import com.jabistudio.androidjhlabs.filter.SmartBlurFilter;
import com.jabistudio.androidjhlabs.filter.SolarizeFilter;
import com.jabistudio.androidjhlabs.filter.util.AndroidUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.socialone.android.MainApp;
import com.socialone.android.R;
import com.socialone.android.appnet.adnlib.Annotations;
import com.socialone.android.appnet.adnlib.AppDotNetClient;
import com.socialone.android.appnet.adnlib.data.Annotation;
import com.socialone.android.appnet.adnlib.data.Post;
import com.socialone.android.appnet.adnlib.response.PostResponseHandler;
import com.socialone.android.fivehundredpx.api.auth.User;
import com.socialone.android.fivehundredpx.api.services.UploadService;
import com.socialone.android.services.TwitterPostService;
import com.socialone.android.tools.TumblrClient;
import com.socialone.android.utils.Constants;
import com.socialone.android.utils.Datastore;
import com.socialone.android.utils.FlickrHelper;
import com.tumblr.jumblr.JumblrClient;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oak.widget.CancelEditText;
import oauth.signpost.OAuth;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: david.hodge
 * Date: 11/2/13
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SocialShareFragment extends SherlockFragment {

    View view;
    Datastore mDatastore;
    RelativeLayout facebookShare;
    RelativeLayout twitterShare;
    RelativeLayout plusShare;
    RelativeLayout appNetShare;
    RelativeLayout myspaceShare;

    Switch facebookSwitch;
    Switch twitterSwitch;
    Switch plusSwitch;
    Switch appNetSwitch;
    Switch myspaceSwitch;
    Switch linkedinSwitch;
    Switch flickrSwitch;
    Switch locationSwtich;
    Switch fiveHundSwitch;
    Switch tumblrSwitch;
    Switch smsSwitch;

    LinearLayout photoShareBtn;
    LinearLayout linkShareBtn;
    LinearLayout moodShareBtn;

    LinearLayout photoShareLayout;
    LinearLayout linkShareLayout;
    LinearLayout moodshareLayout;

    ImageView photoShare;
    ImageView linkShare;
    ImageView moodShare;

    ImageView photoShareImg;
    ImageView photoShareFilterMenu;

    TextView shareTextCount;
    CancelEditText shareField;
    Button shareImageBtn;
    Button shareBtn;
    Button resetShares;

    Button sharePhotoCancelBtn;
    Button shareLinkCancelBtn;
    Button shareMoodCancelBtn;

    Context mContext;
    private UiLifecycleHelper uiHelper;
    Session session;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    SocialAuthAdapter mAuthAdapter;
    SocialAuthAdapter linkAuthAdapter;
    SocialAuthAdapter flickrAuthAdapter;
    SocialAuthAdapter plusAuthAdapter;
    AppDotNetClient client;

    String picturePath;
    private static int RESULT_LOAD_IMAGE = 1;
    boolean addPhoto = false;
    boolean addLink = false;
    boolean addMood = false;

    Location location;
    LocationManager locationManager;
    String lat;
    String lon;
    Uri selectedImage;
    Flickr f;
    ConfigurationBuilder cb;
    User fiveUser;
    JumblrClient jumblrClient;
    SharedPreferences prefs;

    PopupMenu popupMenu;
    private final static int CONTRAST = 1;
    private final static int GAIN = 2;
    private final static int GREYSCALE = 3;
    private final static int SOLARIZE = 4;
    private final static int SMARTBLUR = 5;
    private final static int KALEIDOSCOPE = 6;
    private final static int SHARPEN = 7;
    private final static int DESPECK = 8;
    private final static int CHANNELMIX = 9;

    //camera intent stuff
    private static String root = null;
    private static String imageFolderPath = null;
    private String imageName = null;
    private static Uri fileUri = null;
    private static final int CAMERA_IMAGE_REQUEST = 1;

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            shareTextCount.setText(String.valueOf(s.length()));
        }

        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mContext = getSherlockActivity();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        uiHelper = new UiLifecycleHelper(getSherlockActivity(), new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        });
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.social_share_fragment, container, false);

        facebookShare = (RelativeLayout) view.findViewById(R.id.facebook_share_layout);
        twitterShare = (RelativeLayout) view.findViewById(R.id.twitter_share_layout);
        plusShare = (RelativeLayout) view.findViewById(R.id.plus_share_layout);
        appNetShare = (RelativeLayout) view.findViewById(R.id.app_net_share_layout);
        myspaceShare = (RelativeLayout) view.findViewById(R.id.myspace_share_layout);

        facebookSwitch = (Switch) view.findViewById(R.id.facebook_switch);
        twitterSwitch = (Switch) view.findViewById(R.id.twitter_switch);
        plusSwitch = (Switch) view.findViewById(R.id.plus_switch);
        appNetSwitch = (Switch) view.findViewById(R.id.app_net_switch);
        myspaceSwitch = (Switch) view.findViewById(R.id.myspace_switch);
        linkedinSwitch = (Switch) view.findViewById(R.id.linkedin_switch);
        flickrSwitch = (Switch) view.findViewById(R.id.flickr_switch);
        locationSwtich = (Switch) view.findViewById(R.id.location_switch);
        fiveHundSwitch = (Switch) view.findViewById(R.id.fivehund_switch);
        tumblrSwitch = (Switch) view.findViewById(R.id.tumblr_switch);
        smsSwitch = (Switch) view.findViewById(R.id.sms_switch);

        photoShareBtn = (LinearLayout) view.findViewById(R.id.photo_share_btn);
        linkShareBtn = (LinearLayout) view.findViewById(R.id.link_share_btn);
        moodShareBtn = (LinearLayout) view.findViewById(R.id.mood_share_btn);

        photoShareLayout = (LinearLayout) view.findViewById(R.id.social_share_photo_layout);
        linkShareLayout = (LinearLayout) view.findViewById(R.id.social_share_link_layout);
        moodshareLayout = (LinearLayout) view.findViewById(R.id.social_share_mood_layout);

        photoShare = (ImageView) view.findViewById(R.id.photo_share_icon);
        linkShare = (ImageView) view.findViewById(R.id.link_share_icon);
        moodShare = (ImageView) view.findViewById(R.id.mood_share_icon);

        photoShareImg = (ImageView) view.findViewById(R.id.social_share_photo_view);
        photoShareFilterMenu = (ImageView) view.findViewById(R.id.social_photo_filter_picker);
        shareBtn = (Button) view.findViewById(R.id.social_share_button);
        resetShares = (Button) view.findViewById(R.id.social_reset_button);

        shareLinkCancelBtn = (Button) view.findViewById(R.id.social_share_link_cancel_btn);
        sharePhotoCancelBtn = (Button) view.findViewById(R.id.social_share_photo_cancel_btn);
        shareMoodCancelBtn = (Button) view.findViewById(R.id.social_share_mood_cancel_btn);

        shareTextCount = (TextView) view.findViewById(R.id.share_text_count);
        shareField = (CancelEditText) view.findViewById(R.id.share_details_text);
        shareField.addTextChangedListener(mTextEditorWatcher);

        shareAddThings();

        popupMenu = new PopupMenu(getSherlockActivity(), photoShareFilterMenu);
        popupMenu.getMenu().add(android.view.Menu.NONE, CONTRAST, android.view.Menu.NONE,  "Contrast");
        popupMenu.getMenu().add(android.view.Menu.NONE, GAIN, android.view.Menu.NONE, "Gain");
        popupMenu.getMenu().add(android.view.Menu.NONE, GREYSCALE, android.view.Menu.NONE, "Greyscale");
        popupMenu.getMenu().add(android.view.Menu.NONE, SOLARIZE, android.view.Menu.NONE, "Solarize");
        popupMenu.getMenu().add(android.view.Menu.NONE, SMARTBLUR, android.view.Menu.NONE, "Smart Blur");
        popupMenu.getMenu().add(android.view.Menu.NONE, KALEIDOSCOPE, android.view.Menu.NONE, "Kaleidoscope");
        popupMenu.getMenu().add(android.view.Menu.NONE, SHARPEN, android.view.Menu.NONE, "Sharpen");
        popupMenu.getMenu().add(android.view.Menu.NONE, DESPECK, android.view.Menu.NONE, "Despeckle");
        popupMenu.getMenu().add(android.view.Menu.NONE, CHANNELMIX, android.view.Menu.NONE, "Channel Mix");

        photoShareFilterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case CONTRAST:
                        contrastImage();
                        break;
                    case GAIN:
                        gainImage();
                        break;
                    case GREYSCALE:
                        greyscaleImage();
                        break;
                    case SOLARIZE:
                        solarizeImage();
                        break;
                    case SMARTBLUR:
                        smartBlurImage();
                        break;
                    case KALEIDOSCOPE:
                        kaleidoscopeFilter();
                        break;
                    case SHARPEN:
                        sharpenImage();
                        break;
                    case DESPECK:
                        despeckleImage();
                        break;
                    case CHANNELMIX:
                        channelMixImage();
                        break;
                }
                return false;
            }
        });

        photoShare.setColorFilter(getResources().getColor(R.color.white));
        linkShare.setColorFilter(getResources().getColor(R.color.white));
        moodShare.setColorFilter(getResources().getColor(R.color.white));
//        shareImageBtn = (Button) view.findViewById(R.id.share_details_imagebtn);

        facebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getSherlockActivity(), SocialConnectFragment.class));
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAllThings();
            }
        });

        resetShares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetShareOptions();
            }
        });

        twitterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                twitterSetup();
            }
        });

        linkedinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                linkedinSetup();
            }
        });
        plusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                plusSetup();
            }
        });
        locationSwtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getUserLocation();
            }
        });

        fiveHundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fiveUser = new User();
            }
        });

        photoShareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoShareImg.buildDrawingCache();
                Bitmap mOrigBitmap = photoShareImg.getDrawingCache();
                SolarizeFilter solarFilter = new SolarizeFilter();
                int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
                int width = mOrigBitmap.getWidth();
                int height = mOrigBitmap.getHeight();
                //int[] dest = invertFilter.filter(src, width, height);
                int[] dest = solarFilter.filter(src, width, height);

                Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
                photoShareImg.setImageBitmap(destBitmap);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        flickrSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flickrSetup();
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
    }

    private void resetShareOptions() {
        facebookSwitch.setChecked(false);
        twitterSwitch.setChecked(false);
        plusSwitch.setChecked(false);
        appNetSwitch.setChecked(false);
        myspaceSwitch.setChecked(false);
        tumblrSwitch.setChecked(false);
        flickrSwitch.setChecked(false);
        linkedinSwitch.setChecked(false);
        fiveHundSwitch.setChecked(false);
        smsSwitch.setChecked(false);
        locationSwtich.setChecked(false);
        shareField.setText("");
        photoShareImg.setImageBitmap(null);
        photoShareImg.setImageDrawable(null);
    }

    private void getUserLocation() {
        try {
            locationManager = (LocationManager) getSherlockActivity().getSystemService(getSherlockActivity().LOCATION_SERVICE);
            String bestProvider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(bestProvider);
            lat = Double.toString(location.getLatitude());
            lon = Double.toString(location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void addPhotoPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        builder.setMessage("This will end the activity");
        builder.setCancelable(true);
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                root = Environment.getExternalStorageDirectory().toString()
                        + "/SocialOne_folder";
                // Creating folders for Image
                imageFolderPath = root + "/saved_images";
                File imagesFolder = new File(imageFolderPath);
                imagesFolder.mkdirs();
                // Generating file name
                imageName = "test.png";
                // Creating image here
                File image = new File(imageFolderPath, imageName);
                fileUri = Uri.fromFile(image);
                photoShareImg.setTag(imageFolderPath + File.separator + imageName);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takePictureIntent,
                        CAMERA_IMAGE_REQUEST);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getParentFragment().startActivityForResult(i, RESULT_LOAD_IMAGE);
                photoShareLayout.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void shareAddThings() {
        photoShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoPicker();
            }
        });

        linkShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkShareLayout.setVisibility(View.VISIBLE);
                addLink = true;
            }
        });

        moodShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodshareLayout.setVisibility(View.VISIBLE);
                addMood = true;
            }
        });

        sharePhotoCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoShareLayout.setVisibility(View.GONE);
            }
        });

        shareLinkCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkShareLayout.setVisibility(View.GONE);
            }
        });

        shareMoodCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodshareLayout.setVisibility(View.GONE);
            }
        });

    }

    private void facebookShare(String string) {
        //For facebook sharing
//        String userShareText = shareField.getText().toString();
        session = ensureFacebookSessionFromCache(mContext);

        if (session != null) {
//            Check for publish permissions
//            List<String> permissions = session.getPermissions();
//            if (!isSubsetOf(PERMISSIONS, permissions)) {
//                session.addCallback(new Session.StatusCallback() {
//                    @Override
//                    public void call(Session session, SessionState state, Exception exception) {
//                        if (exception != null) {
////                            uiShowError(getResources().getString(R.string.error_io_exception));
//                            session.removeCallback(this);
//                            return;
//                        }
//                        if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
//                            shareAllThings();
//                            session.removeCallback(this);
//                        }
//                    }
//                });
//                Session.NewPermissionsRequest newPermissionsRequest = new Session
//                        .NewPermissionsRequest(this, PERMISSIONS);
//
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
//                    newPermissionsRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
//                }
//
//                session.requestNewPublishPermissions(newPermissionsRequest);
//                return;
//            }

            //If we're here then we're sharing to facebook!


            if (addPhoto) {
                //text and image post

                JSONObject privacy = new JSONObject();
                try {
                    privacy.put("value", "SELF");
                } catch (Exception e) {
                    Log.e("facebook", "Unknown error while preparing params", e);
                }

                Bundle postParams = new Bundle();
                postParams.putString("message", string);
                postParams.putString("privacy", privacy.toString());
                byte[] data = null;

                Bitmap bi = BitmapFactory.decodeFile(picturePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                data = baos.toByteArray();
                postParams.putByteArray("source", data);

                Request.Callback callback = new Request.Callback() {
                    public void onCompleted(Response response) {

                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Toast.makeText(getSherlockActivity(),
                                    error.getErrorMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getSherlockActivity(),
                                    "Facebook Share completed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };

                Request request = new Request(session, "me/photos", postParams,
                        HttpMethod.POST, callback);

                RequestAsyncTask task = new RequestAsyncTask(request);
                task.execute();
                getSherlockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getSherlockActivity(),
                                "adding facebook picture",
                                Toast.LENGTH_LONG).show();
                    }
                });
            } else {

                //just text post
                Bundle postParams = new Bundle();
                postParams.putString("message", string);
                Request.Callback callback = new Request.Callback() {
                    public void onCompleted(Response response) {

                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Toast.makeText(getSherlockActivity(),
                                    error.getErrorMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getSherlockActivity(),
                                    "Facebook Share completed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };
                Request request = new Request(session, "me/feed", postParams,
                        HttpMethod.POST, callback);

                RequestAsyncTask task = new RequestAsyncTask(request);
                task.execute();
            }

//            Request.Callback callback = new Request.Callback() {
//                public void onCompleted(Response response) {
//
//                    FacebookRequestError error = response.getError();
//                    if (error != null) {
//                        Toast.makeText(getSherlockActivity(),
//                                error.getErrorMessage(),
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getSherlockActivity(),
//                                "Facebook Share completed",
//                                Toast.LENGTH_LONG).show();
//                    }
//                }
//            };
//
//            Request request = new Request(session, "me/feed", postParams,
//                    HttpMethod.POST, callback);
//
//            RequestAsyncTask task = new RequestAsyncTask(request);
//            task.execute();
        }
    }

    private void twitterSetup() {
        mAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Log.d("twitter", "auth adapter completed");
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
        mAuthAdapter.authorize(mContext, SocialAuthAdapter.Provider.TWITTER);
    }

    private void twitterShare(String string) {
        final String share = string;
        byte[] data = null;

        //Note that at times this
        if (addPhoto) {
            try {
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
                TwitterFactory tf = new TwitterFactory(cb.build());
                Twitter twitter = tf.getInstance();
                StatusUpdate statusUpdate = new StatusUpdate(string);
                if (locationSwtich.isChecked()) {
                    statusUpdate.setLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
                    statusUpdate.setDisplayCoordinates(true);
                }
                statusUpdate.setMedia("userimg.jpg", bs);
                Status status = twitter.updateStatus(statusUpdate);
            } catch (Exception e) {
                Log.e("twitter", e.toString());
            }
        } else {

            try {


                Log.d("twitter", share);
                cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey(Constants.TWIT_CONSUMER_KEY)
                        .setOAuthConsumerSecret(Constants.TWIT_CONSUMER_SECRET)
                        .setOAuthAccessToken(mAuthAdapter.getCurrentProvider().getAccessGrant().getKey())
                        .setOAuthAccessTokenSecret(mAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
                TwitterFactory tf = new TwitterFactory(cb.build());
                Twitter twitter = tf.getInstance();
                StatusUpdate statusUpdate = new StatusUpdate(string);
                if (locationSwtich.isChecked()) {
                    statusUpdate.setLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
                    statusUpdate.setDisplayCoordinates(true);
                }
                Status status = twitter.updateStatus(statusUpdate);
            } catch (Exception e) {
                Log.e("twitter", e.toString());
            }
        }


//        //Note that at times this
//        if(addPhoto){
//            byte[] data = null;
//
//            Log.d("photo", picturePath);
//            Bitmap bi = BitmapFactory.decodeFile(picturePath);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            data = baos.toByteArray();
//            try{
//                mAuthAdapter.uploadImageAsync(string, "userImage.jpg", bi, 30, new SocialAuthListener<Integer>() {
//                    @Override
//                    public void onExecute(String s, Integer status) {
//                        if (status == 200 || status == 201 || status == 204) {
//                            Toast.makeText(getSherlockActivity(),
//                                    "Twitter Share completed",
//                                    Toast.LENGTH_LONG).show();
//                            shareField.getText().clear();
//                        } else {
//                            Log.e("twitter", "Error updating twitter status=" + status);
//                        }
//
//                    }
//
//                    @Override
//                    public void onError(SocialAuthError socialAuthError) {
//
//                    }
//                });
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }else{
//            mAuthAdapter.updateStatus(string, new SocialAuthListener<Integer>() {
//                @Override
//                public void onExecute(String s, Integer status) {
//                    if (status == 200 || status == 201 || status == 204) {
//                        Toast.makeText(getSherlockActivity(),
//                            "Twitter Share completed",
//                            Toast.LENGTH_LONG).show();
//                        shareField.getText().clear();
//                    } else {
//                        Log.e("twitter", "Error updating twitter status=" + status);
//                    }
//                }
//
//                @Override
//                public void onError(SocialAuthError socialAuthError) {
//                    Log.e("twitter", "Error updating twitter", socialAuthError);
//                }
//            }, false);
//        }
    }

    private void plusSetup() {
        plusAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Log.d("twitter", "auth adapter completed");
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
        plusAuthAdapter.addCallBack(SocialAuthAdapter.Provider.GOOGLEPLUS, Constants.PLUS_CALLBACK);
        plusAuthAdapter.authorize(mContext, SocialAuthAdapter.Provider.GOOGLEPLUS);

    }

    private void plusShare(String string) {
        plusAuthAdapter.updateStatus(string, new SocialAuthListener<Integer>() {
            @Override
            public void onExecute(String s, Integer status) {
                if (status == 200 || status == 201 || status == 204) {
                    Toast.makeText(getSherlockActivity(),
                            "Twitter Share completed",
                            Toast.LENGTH_LONG).show();
                    shareField.getText().clear();
                } else {
                    Log.e("googleplus", "Error updating google plus status=" + status);
                }
            }

            @Override
            public void onError(SocialAuthError socialAuthError) {
                Log.e("googleplus", "Error updating google plus", socialAuthError);
            }
        }, true);
    }

    private void appNetShare(String string) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        Post post = new Post(string);
        if (locationSwtich.isChecked()) {
            HashMap<String, Object> list2 = new HashMap<String, Object>();
            list2.put("latitude", lat);
            list2.put("longitude", lon);
            Annotation annotation = new Annotation();
            annotation.setType(Annotations.GEOLOCATION);
            annotation.setValue(list2);
            post.addAnnotation(annotation);
        }
        client = new AppDotNetClient(prefs.getString(OAuth.OAUTH_TOKEN, null));
        client.createPost(post, new PostResponseHandler() {
            @Override
            public void onSuccess(Post responseData) {
                getSherlockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getSherlockActivity(),
                                "App.net Share completed",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void myspaceShare(String string) {
        //TODO
    }

    private void linkedinSetup() {

        linkAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Log.d("twitter", "auth adapter completed");
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
        linkAuthAdapter.addCallBack(SocialAuthAdapter.Provider.LINKEDIN, Constants.LINKEDIN_CALLBACK);
        linkAuthAdapter.authorize(mContext, SocialAuthAdapter.Provider.LINKEDIN);

    }

    private void linkedinShare(String string) {

        linkAuthAdapter.updateStatus(string, new SocialAuthListener<Integer>() {
            @Override
            public void onExecute(String s, Integer status) {
                Log.d("linkedin", s + Integer.toString(status));
                if (status == 200 || status == 201 || status == 204) {
                    Toast.makeText(getSherlockActivity(),
                            "Linkedin Share completed",
                            Toast.LENGTH_LONG).show();
                    shareField.getText().clear();
                } else {
                    Log.e("linkedin", "Error updating linkedin status=" + status);
                }
            }

            @Override
            public void onError(SocialAuthError socialAuthError) {
                Log.e("linkedin", socialAuthError.getMessage());

            }
        }, false);
    }

    private void flickrSetup() {
        flickrAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                getSherlockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("flickr", "flickr complete");
                        f = FlickrHelper.getInstance().getFlickrAuthed(
                                flickrAuthAdapter.getCurrentProvider().getAccessGrant().getKey(),
                                flickrAuthAdapter.getCurrentProvider().getAccessGrant().getSecret());
                    }
                });
            }

            @Override
            public void onError(SocialAuthError socialAuthError) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onBack() {

            }
        });
        flickrAuthAdapter.addCallBack(SocialAuthAdapter.Provider.FLICKR, Constants.FLICKR_CALLBACK);
        flickrAuthAdapter.authorize(mContext, SocialAuthAdapter.Provider.FLICKR);
    }

    private void flickrShare(String string) {

        byte[] data = null;

        Bitmap bi = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data = baos.toByteArray();
        try {
            f.getUploader().upload("image.jpg", data, new UploadMetaData());

        } catch (Exception e) {
            Log.d("flickr", e.toString());
        }

//        try{
//            flickrAuthAdapter.uploadImageAsync(string, "userImage.jpg", bi, 30, new SocialAuthListener<Integer>() {
//                @Override
//                public void onExecute(String s, Integer status) {
//                    if (status == 200 || status == 201 || status == 204) {
//                        Toast.makeText(getSherlockActivity(),
//                                "flickr Share completed",
//                                Toast.LENGTH_LONG).show();
//                    } else {
//                        Log.e("flickr", "Error updating flickr status=" + status);
//                    }
//
//                }
//
//                @Override
//                public void onError(SocialAuthError socialAuthError) {
//
//                }
//            });
//        }catch (Exception e){
//           Log.e("flickr", e.toString());
//        }
    }

    private boolean mIsBound;

    private void fiveHundShare() {
        byte[] data = null;
        Bitmap bi = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data = baos.toByteArray();

        Intent i = new Intent(getSherlockActivity(), UploadService.class);
        i.putExtra("selectedImageUri", data);
        i.putExtra("accessToken", fiveUser.accessToken);
        i.putExtra("title", "test upload");
        i.putExtra("description", "testing image upload");

//                bindService(i, mConnection, Context.BIND_AUTO_CREATE);

        getSherlockActivity().startService(i);
        mIsBound = true;
        onFinishTask();

    }

    private void tumblrShare(String string) {
        try {
            Map<String, String> detail = new HashMap<String, String>();
            detail.put("quote", string);
            detail.put("type", "quote");
            jumblrClient.postCreate(jumblrClient.user().getName(), detail);
        } catch (Exception e) {
            Log.e("tumblr", e.toString());
        }
    }

    public void onFinishTask() {
        //TODO
        Log.d("500px", "image upload completed");
    }

    public void shareAllThings() {
        String userShareText = shareField.getText().toString();

        if (facebookSwitch.isChecked()) {

            if (smsSwitch.isChecked()) {
                int phoneNo = 32655;
                String sms = userShareText;
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(Integer.toString(phoneNo), null, sms, null, null);
                    Toast.makeText(getSherlockActivity(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getSherlockActivity(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getSherlockActivity(),
                        "sharing " + userShareText + " to facebook",
                        Toast.LENGTH_LONG).show();
                facebookShare(userShareText);
            }
        }

        if (twitterSwitch.isChecked()) {

            if (smsSwitch.isChecked()) {
                int phoneNo = 40404;
                String sms = userShareText;

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(Integer.toString(phoneNo), null, sms, null, null);
                    Toast.makeText(getSherlockActivity(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getSherlockActivity(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                Intent twitterShare = new Intent(getSherlockActivity(), TwitterPostService.class);
                twitterShare.putExtra("share_text", userShareText);
                if (addPhoto) {
                    twitterShare.putExtra("addPhoto", addPhoto);
                    twitterShare.putExtra("picturePath", picturePath);
                }
                if (locationSwtich.isChecked()) {
                    twitterShare.putExtra("addLocation", true);
                }
                getSherlockActivity().startService(twitterShare);
            }
        }

        if (plusSwitch.isChecked()) {

            if (smsSwitch.isChecked()) {
                int phoneNo = 33669;
                String sms = userShareText;

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(Integer.toString(phoneNo), null, sms, null, null);
                    Toast.makeText(getSherlockActivity(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getSherlockActivity(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                Intent shareIntent = new PlusShare.Builder(mContext)
                        .setType("text/plain")
                        .setText(userShareText)
                        .getIntent();
                startActivityForResult(shareIntent, 0);
            }

            //plus share builder
//            PlusShare.Builder builder = new PlusShare.Builder(this, monPlusClient);
//            builder.addCallToAction("DISCOVER", Uri.parse(target), ident);
//            builder.setContentUrl(Uri.parse(target));
//            builder.setContentDeepLinkId(ident, topic.getNomPeintre(), desc,
//                    Uri.parse(topic.getLien()));
//            builder.setText(texte + " #Art");
//            Intent shareIntent = builder.getIntent();
//            startActivityForResult(shareIntent, SHAREGPLUS_REQUEST_CODE);

//            plusShare(userShareText);
        }

        if (appNetSwitch.isChecked()) {
            if (userShareText.length() > 256) {
                Toast.makeText(getSherlockActivity(),
                        "Sharing to App.net requires 256 or less characters",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getSherlockActivity(),
                        "sharing " + userShareText + " to app.net",
                        Toast.LENGTH_LONG).show();
                appNetShare(userShareText);
            }
        }

        if (myspaceSwitch.isChecked()) {

            if (smsSwitch.isChecked()) {
                int phoneNo = 69772;
                String sms = "shout " + userShareText;

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(Integer.toString(phoneNo), null, sms, null, null);
                    Toast.makeText(getSherlockActivity(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                }catch (Exception e) {
                    Toast.makeText(getSherlockActivity(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                myspaceShare(userShareText);
            }
        }

        if (linkedinSwitch.isChecked()) {
            linkedinShare(userShareText);
        }

        if (flickrSwitch.isChecked()) {
            flickrShare(userShareText);
        }

        if (fiveHundSwitch.isChecked()) {
            fiveHundShare();
        }

        if (tumblrSwitch.isChecked()) {
            photoShareImg.setDrawingCacheEnabled(true);
            Bitmap tumblrBitmap = photoShareImg.getDrawingCache();
            jumblrClient = new JumblrClient(Constants.TUMBLR_CONSUMER_KEY, Constants.TUMBLR_CONSUMER_SECRET);
            jumblrClient.setToken(prefs.getString(Constants.TUMBLR_ACCESS, null), prefs.getString(Constants.TUMBLR_SECRET, null));
            TumblrClient tumblrClient = MainApp.getClient();
            tumblrClient.createPhotoPost(jumblrClient.user().getName(), tumblrBitmap, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int arg0, String arg1) {
                   Toast.makeText(getSherlockActivity(), "Image posted to tumblr", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(Throwable arg0, String arg1) {
                    Toast.makeText(getSherlockActivity(), arg1, Toast.LENGTH_LONG).show();
                }
            });

            tumblrShare(userShareText);
        }

        resetShareOptions();
//        shareField.getText().clear();

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        return;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.social_share_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                shareAllThings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
//            Toast.makeText(getSherlockActivity(), "connected", Toast.LENGTH_SHORT).show();
//            getUserInfo();
            Request meRequest = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        Toast.makeText(mContext, "Welcome back " + user.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            meRequest.executeAndWait();
        } else if (state.isClosed()) {
            Toast.makeText(getSherlockActivity(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException,
            IOException {
        InputStream input = getSherlockActivity().getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;// optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1)
                || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
                : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 200) ? (originalSize / 200)
                : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;// optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
        input = getSherlockActivity().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0)
            return 1;
        else
            return k;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("photo", "activity for result called");
        try {
            if (!TextUtils.isEmpty(data.getData().toString())) {
                Log.d("photo", "got dat photo");
                selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getSherlockActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
                Log.d("photo", picturePath);
                photoShareImg.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//                photoShareImg.setImageBitmap(selectedImage);
                addPhoto = true;
            } else {
                Log.d("photo", "result fail");
                addPhoto = false;
                photoShareLayout.setVisibility(View.GONE);
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//            Cursor cursor = getSherlockActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            picturePath = cursor.getString(columnIndex);
//            cursor.close();
//            photoShareImg.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }
        } catch (Exception e) {
            addPhoto = false;
            e.printStackTrace();
            photoShareLayout.setVisibility(View.GONE);
        }
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(getSherlockActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(getSherlockActivity());
    }

    public boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    public static Session ensureFacebookSessionFromCache(Context context) {
        Session activeSession = Session.getActiveSession();
        if (activeSession == null || !activeSession.getState().isOpened()) {
            activeSession = Session.openActiveSessionFromCache(context);
        }
        return activeSession;
    }

    public void solarizeImage(){
        photoShareImg.buildDrawingCache();
        Bitmap mOrigBitmap = photoShareImg.getDrawingCache();
        SolarizeFilter solarFilter = new SolarizeFilter();
        int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
        int width = mOrigBitmap.getWidth();
        int height = mOrigBitmap.getHeight();
        //int[] dest = invertFilter.filter(src, width, height);
        int[] dest = solarFilter.filter(src, width, height);

        Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
        photoShareImg.setImageBitmap(destBitmap);
    }

    public void contrastImage(){
        photoShareImg.buildDrawingCache();
        Bitmap mOrigBitmap = photoShareImg.getDrawingCache();
        ContrastFilter contrastFilter = new ContrastFilter();
        int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
        int width = mOrigBitmap.getWidth();
        int height = mOrigBitmap.getHeight();
        //int[] dest = invertFilter.filter(src, width, height);
        int[] dest = contrastFilter.filter(src, width, height);

        Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
        photoShareImg.setImageBitmap(destBitmap);
    }

    public void gainImage(){
        photoShareImg.buildDrawingCache();
        Bitmap mOrigBitmap = photoShareImg.getDrawingCache();
        GainFilter gainFilter = new GainFilter();
        int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
        int width = mOrigBitmap.getWidth();
        int height = mOrigBitmap.getHeight();
        //int[] dest = invertFilter.filter(src, width, height);
        int[] dest = gainFilter.filter(src, width, height);

        Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
        photoShareImg.setImageBitmap(destBitmap);
    }

    public void greyscaleImage(){
        photoShareImg.buildDrawingCache();
        Bitmap mOrigBitmap = photoShareImg.getDrawingCache();
        GrayscaleFilter grayscaleFilter = new GrayscaleFilter();
        int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
        int width = mOrigBitmap.getWidth();
        int height = mOrigBitmap.getHeight();
        //int[] dest = invertFilter.filter(src, width, height);
        int[] dest = grayscaleFilter.filter(src, width, height);

        Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
        photoShareImg.setImageBitmap(destBitmap);
    }

    public void sharpenImage(){
        photoShareImg.buildDrawingCache();
        Bitmap mOrigBitmap = photoShareImg.getDrawingCache();
        SharpenFilter sharpenFilters = new SharpenFilter();
        int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
        int width = mOrigBitmap.getWidth();
        int height = mOrigBitmap.getHeight();
        //int[] dest = invertFilter.filter(src, width, height);
        int[] dest = sharpenFilters.filter(src, width, height);

        Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
        photoShareImg.setImageBitmap(destBitmap);
    }

    public void smartBlurImage(){
        photoShareImg.buildDrawingCache();
        Bitmap mOrigBitmap = photoShareImg.getDrawingCache();
        SmartBlurFilter smartBlurFilter = new SmartBlurFilter();
        int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
        int width = mOrigBitmap.getWidth();
        int height = mOrigBitmap.getHeight();
        //int[] dest = invertFilter.filter(src, width, height);
        int[] dest = smartBlurFilter.filter(src, width, height);

        Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
        photoShareImg.setImageBitmap(destBitmap);
    }

    public void kaleidoscopeFilter(){
        photoShareImg.buildDrawingCache();
        Bitmap mOrigBitmap = photoShareImg.getDrawingCache();
        KaleidoscopeFilter kaleidoscopeFilter = new KaleidoscopeFilter();
        int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
        int width = mOrigBitmap.getWidth();
        int height = mOrigBitmap.getHeight();
        //int[] dest = invertFilter.filter(src, width, height);
        int[] dest = kaleidoscopeFilter.filter(src, width, height);

        Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
        photoShareImg.setImageBitmap(destBitmap);
    }

    public void channelMixImage(){
        photoShareImg.buildDrawingCache();
        Bitmap mOrigBitmap = photoShareImg.getDrawingCache();
        ChannelMixFilter channelMixFilter = new ChannelMixFilter();
        int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
        int width = mOrigBitmap.getWidth();
        int height = mOrigBitmap.getHeight();
        //int[] dest = invertFilter.filter(src, width, height);
        int[] dest = channelMixFilter.filter(src, width, height);

        Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
        photoShareImg.setImageBitmap(destBitmap);
    }

    public void despeckleImage(){
        photoShareImg.buildDrawingCache();
        Bitmap mOrigBitmap = photoShareImg.getDrawingCache();
        DespeckleFilter despeckleFilter = new DespeckleFilter();
        int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
        int width = mOrigBitmap.getWidth();
        int height = mOrigBitmap.getHeight();
        //int[] dest = invertFilter.filter(src, width, height);
        int[] dest = despeckleFilter.filter(src, width, height);

        Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
        photoShareImg.setImageBitmap(destBitmap);
    }

}
