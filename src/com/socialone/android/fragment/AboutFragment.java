package com.socialone.android.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.socialone.android.R;

/**
 * Created by david.hodge on 1/6/14.
 */
public class AboutFragment extends SherlockFragment {

    View view;
    TextView buildText;

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

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
