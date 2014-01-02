package com.socialone.android.condesales.listeners;


import com.socialone.android.condesales.models.Checkin;

public interface CheckInListener extends ErrorListener {

	public void onCheckInDone(Checkin checkin);
	
}
