package com.socialone.android.jinstagram.entity.likes;

import com.google.gson.annotations.SerializedName;
import com.socialone.android.jinstagram.InstagramObject;
import com.socialone.android.jinstagram.entity.common.Meta;
import com.socialone.android.jinstagram.entity.common.User;

import java.util.List;

public class LikesFeed extends InstagramObject {

	@SerializedName("meta")
	private Meta meta;

	@SerializedName("data")
	private List<User> userList;

	/**
	 * @return the meta
	 */
	public Meta getMeta() {
		return meta;
	}

	/**
	 * @param meta
	 *            the meta to set
	 */
	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	/**
	 * @return the userList
	 */
	public List<User> getUserList() {
		return userList;
	}

	/**
	 * @param userList
	 *            the userList to set
	 */
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

    @Override
    public String toString() {
        return String.format("LikesFeed [meta=%s, userList=%s]", meta, userList);
    }
}
