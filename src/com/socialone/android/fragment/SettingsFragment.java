package com.socialone.android.fragment;

import android.os.Bundle;

import com.socialone.android.R;

/**
 * Created by david.hodge on 5/3/14.
 */
public class SettingsFragment extends android.support.v4.preference.PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
