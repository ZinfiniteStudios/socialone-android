package com.socialone.android.condesales.listeners;

import com.socialone.android.condesales.models.User;

public interface UserInfoRequestListener extends ErrorListener {

	public void onUserInfoFetched(User user);
}
