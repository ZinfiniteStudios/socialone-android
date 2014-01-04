package com.socialone.android.fivehundredpx.api.auth;

import com.socialone.android.fivehundredpx.api.FiveHundredException;

import org.apache.http.client.methods.HttpPost;


public interface OAuthProvider {
	void signForAccessToken(HttpPost req) throws FiveHundredException;
	
	void setOAuthConsumer(String consumerKey, String consumerSecret);
	void setOAuthRequestToken(String requestTokenKey, String requestTokenSecret);
}