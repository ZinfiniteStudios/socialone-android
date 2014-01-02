package com.socialone.android.condesales.listeners;

import com.socialone.android.condesales.models.Tip;

import java.util.ArrayList;


public interface TipsResquestListener extends ErrorListener {
	
	public void onTipsFetched(ArrayList<Tip> tips);
	
}
