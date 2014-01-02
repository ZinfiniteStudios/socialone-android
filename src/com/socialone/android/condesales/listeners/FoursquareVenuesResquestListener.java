package com.socialone.android.condesales.listeners;

import com.socialone.android.condesales.models.Venue;

import java.util.ArrayList;


public interface FoursquareVenuesResquestListener extends ErrorListener {
	
	public void onVenuesFetched(ArrayList<Venue> venues);
	
}
