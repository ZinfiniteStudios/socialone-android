package com.socialone.android.jinstagram.entity.media;

import com.google.gson.annotations.SerializedName;
import com.socialone.android.jinstagram.InstagramObject;
import com.socialone.android.jinstagram.entity.common.Meta;
import com.socialone.android.jinstagram.entity.users.feed.MediaFeedData;


public class MediaInfoFeed extends InstagramObject {
	@SerializedName("data")
	private MediaFeedData data;

	@SerializedName("meta")
	private Meta meta;

	/**
	 * @return the meta
	 */
	public Meta getMeta() {
		return meta;
	}

	/**
	 * @param meta the meta to set
	 */
	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	/**
	 * @return the data
	 */
	public MediaFeedData getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(MediaFeedData data) {
		this.data = data;
	}

    @Override
    public String toString() {
        return String.format("MediaInfoFeed [data=%s, meta=%s]", data, meta);
    }
}
