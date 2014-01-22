package com.socialone.android.jinstagram.entity.users.basicinfo;

import com.google.gson.annotations.SerializedName;
import com.socialone.android.jinstagram.InstagramObject;


public class UserInfo extends InstagramObject {
	@SerializedName("data")
	private UserInfoData data;

	public UserInfoData getData() {
		return data;
	}

	public void setData(UserInfoData data) {
		this.data = data;
	}

    @Override
    public String toString() {
        return String.format("UserInfo [data=%s]", data);
    }
}
