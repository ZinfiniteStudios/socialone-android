package com.socialone.android.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.jberkel.pay.me.IabHelper;
import com.github.jberkel.pay.me.IabResult;
import com.github.jberkel.pay.me.listener.OnIabPurchaseFinishedListener;
import com.github.jberkel.pay.me.listener.OnIabSetupFinishedListener;
import com.github.jberkel.pay.me.model.ItemType;
import com.github.jberkel.pay.me.model.Purchase;
import com.socialone.android.R;
import com.socialone.android.utils.Constants;

/**
 * Created by david.hodge on 5/3/14.
 */
public class StoreFragment extends Fragment {

    View view;
    Button adsBtn;
    Button twitterBtn;
    Button donateBtn;

    IabHelper mIabHelper;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIabHelper = new IabHelper(getActivity(), Constants.BASE_PURCHASE_KEY);
        mIabHelper.startSetup(new OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if(result.isSuccess()){

                }else{

                }
            }
        });
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        edit = prefs.edit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.store_fragment, container, false);
        adsBtn = (Button) view.findViewById(R.id.ads_btn);
        twitterBtn = (Button) view.findViewById(R.id.twitter_btn);
        donateBtn = (Button) view.findViewById(R.id.donate_btn);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        adsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIabHelper.launchPurchaseFlow(getActivity(), "remove_ads", ItemType.INAPP, 0, new OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                                if(result.isSuccess()){
                                    android.util.Log.d("socialone", "remove ad purchase success!");
                                    edit.putBoolean("ads", true);
                                    edit.commit();
                                }else{
                                    android.util.Log.d("socialone", "remove ad purchase failed!");
                                    edit.putBoolean("ads", false);
                                    edit.commit();
                                }
                            }
                        }, null);
            }
        });

        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIabHelper.launchPurchaseFlow(getActivity(), "unlock_twitter", ItemType.INAPP, 0, new OnIabPurchaseFinishedListener() {
                    @Override
                    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                        if (result.isSuccess()) {
                            android.util.Log.d("socialone", "unlock twitter purchase success!");
                            edit.putBoolean("twit_p", true);
                            edit.commit();
                        } else {
                            android.util.Log.d("socialone", "unlock twitter purchase failed");
                            edit.putBoolean("twit_p", false);
                            edit.commit();
                        }
                    }
                }, null);
            }
        });

        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIabHelper.launchPurchaseFlow(getActivity(), "donate", ItemType.INAPP, 0, new OnIabPurchaseFinishedListener() {
                    @Override
                    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                        if (result.isSuccess()) {
                            android.util.Log.d("socialone", "donate purchase success!");
                        } else {
                            android.util.Log.d("socialone", "donate purchase failed");
                        }
                    }
                }, null);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mIabHelper.handleActivityResult(requestCode, resultCode, data);
    }
}
