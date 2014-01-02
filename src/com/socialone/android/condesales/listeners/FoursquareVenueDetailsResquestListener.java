package com.socialone.android.condesales.listeners;


import com.socialone.android.condesales.models.Venue;

public interface FoursquareVenueDetailsResquestListener extends ErrorListener {
	
	public void onVenueDetailFetched(Venue venues);
	
}
