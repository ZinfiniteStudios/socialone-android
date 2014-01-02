package com.socialone.android.condesales.listeners;

import com.socialone.android.condesales.models.User;

import java.util.ArrayList;


public interface FriendsListener extends ErrorListener {

	public void onGotFriends(ArrayList<User> list);
	
}
