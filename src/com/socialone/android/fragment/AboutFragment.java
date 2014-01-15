package com.socialone.android.fragment;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.socialone.android.R;
import com.uservoice.uservoicesdk.UserVoice;
//import com.uservoice.uservoicesdk.UserVoice;

/**
 * Created by david.hodge on 1/6/14.
 */
public class AboutFragment extends SherlockFragment {

    View view;
    TextView buildText;
    Button userVoiceBtn;
    Dialog dialog;
    Button userPortal;
    Button userForum;
    Button userContactUs;
    Button userPostIdea;

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
        userVoiceBtn = (Button) view.findViewById(R.id.user_voice_btn);

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

        userVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userVoiceDialog();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void userVoiceDialog(){
        dialog = new Dialog(getSherlockActivity());
        dialog.setContentView(R.layout.user_voice_dialog);
        userPortal = (Button) dialog.findViewById(R.id.user_voice_portal);
        userForum = (Button) dialog.findViewById(R.id.user_voice_forum);
        userContactUs = (Button) dialog.findViewById(R.id.user_voice_contact);
        userPostIdea = (Button) dialog.findViewById(R.id.user_voice_post_idea);

        userPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserVoice.launchUserVoice(getSherlockActivity());
                dialog.dismiss();
            }
        });

        userForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserVoice.launchForum(getSherlockActivity());
                dialog.dismiss();
            }
        });

        userContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserVoice.launchContactUs(getSherlockActivity());
                dialog.dismiss();
            }
        });

        userPostIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserVoice.launchPostIdea(getSherlockActivity());
                dialog.dismiss();
            }
        });

        dialog.setTitle("User Voice");
        dialog.show();
    }
}
