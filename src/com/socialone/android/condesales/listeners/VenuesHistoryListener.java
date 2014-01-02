package com.socialone.android.condesales.listeners;

import com.socialone.android.condesales.models.Venues;

import java.util.ArrayList;

public interface VenuesHistoryListener extends ErrorListener {

	public void onGotVenuesHistory(ArrayList<Venues> list);

}
