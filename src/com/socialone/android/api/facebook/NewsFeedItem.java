package com.socialone.android.api.facebook;

import com.facebook.model.GraphObject;

/**
 * Created by david.hodge on 1/5/14.
 */
public interface NewsFeedItem extends GraphObject {

    String getID();
    String getName();
    NewsFeedItemFrom getFrom();
    String getMessage();
    String getType();
    NewsFeedItemVia getVia();
    NewsFeedItemApplication getApplication();
    String getCaption();
    String getDescription();
    String getSource();
    String getStatusType();
    String getStory();
    String getPicture();
    String getCreated_Time();
    String getUpdated_Time();

}
