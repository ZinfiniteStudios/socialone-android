package com.socialone.android.api.facebook;

import com.facebook.model.GraphObject;

/**
 * Created by david.hodge on 1/5/14.
 */
public interface NewsFeedItemApplication extends GraphObject {
    String getID();
    String getName();
    String getNamespace();
}