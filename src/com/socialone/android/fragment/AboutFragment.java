package com.socialone.android.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.socialone.android.R;
import com.socialone.android.utils.Constants;

/**
 * Created by david.hodge on 1/6/14.
 */
public class AboutFragment extends SherlockFragment {

    View view;
    TextView buildText;

    TextView dhodgeText;
    TextView twitterText;
    TextView appNetText;
    TextView emailText;
    TextView plusText;
    TextView shareText;
    TextView rateAppText;
    TextView otherAppsText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.about_fragment, container, false);

        buildText = (TextView) view.findViewById(R.id.build_version_text);

        dhodgeText = (TextView) view.findViewById(R.id.dhodge_text);
        twitterText = (TextView) view.findViewById(R.id.twitter_text);
        appNetText = (TextView) view.findViewById(R.id.app_net_text);
        emailText = (TextView) view.findViewById(R.id.email_text);
        plusText = (TextView) view.findViewById(R.id.plus_text);
        shareText = (TextView) view.findViewById(R.id.share_text);
        rateAppText = (TextView) view.findViewById(R.id.rate_text);
        otherAppsText = (TextView) view.findViewById(R.id.other_apps_text);

        String appVersionName = "x.x";
        int appVersionCode = 0;
        try {
            appVersionName = getSherlockActivity().getApplication().getPackageManager()
                    .getPackageInfo(getSherlockActivity().getApplication().getPackageName(), 0).versionName;
            appVersionCode = getSherlockActivity().getApplication().getPackageManager()
                    .getPackageInfo(getSherlockActivity().getApplication().getPackageName(), 0).versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            //Failed
        }
        buildText.setText(
                String.format(getString(R.string.version_dynamic), appVersionName + " b" + appVersionCode));

        dhodgeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DHODGE_URL)));
            }
        });

        twitterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TWITTER_URL)));
            }
        });

        appNetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_NET_URL)));
            }
        });

        emailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", Constants.EMAIL_URL, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SocialOne " + "v1.0.1");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        plusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PLUS_URL)));
            }
        });

        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_RATE_URL)));
            }
        });

        rateAppText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_RATE_URL)));
            }
        });

        otherAppsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.OTHER_APPS_URL)));
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
