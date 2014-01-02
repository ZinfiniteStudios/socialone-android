package com.socialone.android.condesales.listeners;

import com.socialone.android.condesales.models.Venue;

import java.util.ArrayList;

public interface FoursquareTrendingVenuesResquestListener extends ErrorListener {
	
	public void onTrendedVenuesFetched(ArrayList<Venue> venues);
	
}
