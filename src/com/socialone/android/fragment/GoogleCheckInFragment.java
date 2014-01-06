package com.socialone.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.socialone.android.R;

/**
 * Created by david.hodge on 12/25/13.
 */
public class GoogleCheckInFragment extends SherlockFragment {

    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.checkin_list, container, false);
        return view;
    }
}
