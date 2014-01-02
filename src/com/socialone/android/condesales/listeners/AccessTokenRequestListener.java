package com.socialone.android.condesales.listeners;

public interface AccessTokenRequestListener extends ErrorListener {

	public void onAccessGrant(String accessToken);
	
}
