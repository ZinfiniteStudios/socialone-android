package com.socialone.android.jinstagram.realtime;

import com.google.gson.annotations.SerializedName;
import com.socialone.android.jinstagram.entity.common.Meta;


public class SubscriptionResponse {

	@SerializedName("meta")
	private Meta meta;

	@SerializedName("data")
	private SubscriptionResponseData data;

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
	public SubscriptionResponseData getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(SubscriptionResponseData data) {
		this.data = data;
	}

}
