package com.socialone.android.condesales.listeners;

import com.socialone.android.condesales.models.Checkin;

import java.util.ArrayList;


public interface GetCheckInsListener extends ErrorListener {

	public void onGotCheckIns(ArrayList<Checkin> list);
	
}
