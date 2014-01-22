package com.socialone.android.jinstagram.entity.relationships;

import com.google.gson.annotations.SerializedName;
import com.socialone.android.jinstagram.InstagramObject;
import com.socialone.android.jinstagram.entity.common.Meta;


public class RelationshipFeed extends InstagramObject {
	@SerializedName("data")
	private RelationshipData data;

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
	public RelationshipData getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(RelationshipData data) {
		this.data = data;
	}

    @Override
    public String toString() {
        return String.format("RelationshipFeed [data=%s, meta=%s]", data, meta);
    }
}
