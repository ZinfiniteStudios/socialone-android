package com.socialone.android.viewcomponents;

/**
 * Created by david.hodge on 12/18/13.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialone.android.MainApp;
import com.socialone.android.R;

public class NavDrawerItem extends LinearLayout {

    private int mId;
    private String mText;
    private OnClickListener mOnClickListener;
    private View mSelector;

    public NavDrawerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater.from(context).inflate(R.layout.nav_drawer_item, this, true);

        int textRes = attrs.getAttributeResourceValue(MainApp.XMLNS, "itemText", -1);
        if (textRes != -1) {
            mText = context.getString(textRes);
        } else {
            mText = attrs.getAttributeValue(MainApp.XMLNS, "itemText");
        }
        if (mText == null) {
            throw new IllegalArgumentException("Attribute itemText is required.");
        }
        ((TextView) findViewById(R.id.text)).setText(mText);

        int iconResId = attrs.getAttributeResourceValue(MainApp.XMLNS, "itemIcon", -1);
        if (iconResId != -1) {
            ((ImageView) findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(iconResId));
        }
    }

    public NavDrawerItem(Context context, int id) {
        super(context);
        setMyId(id);
    }

    public int getMyId() {
        return mId;
    }

    public void setMyId(int myId) {
        mId = myId;
    }

    public String getText() {
        return mText;
    }

    public void setText(int textResId) {
        mText = getContext().getString(textResId);
        if ((findViewById(R.id.text) != null)) {
            ((TextView) findViewById(R.id.text)).setText(textResId);
        }
    }

    public void setText(String text) {
        mText = text;
        if ((findViewById(R.id.text) != null)) {
            ((TextView) findViewById(R.id.text)).setText(text);
        }
    }
}
