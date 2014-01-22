package com.socialone.android.jinstagram.entity.locations;

import com.google.gson.annotations.SerializedName;
import com.socialone.android.jinstagram.InstagramObject;
import com.socialone.android.jinstagram.entity.common.Location;

import java.util.List;

public class LocationSearchFeed extends InstagramObject {

	@SerializedName("data")
	private List<Location> locationList;

	/**
	 * @return the locationList
	 */
	public List<Location> getLocationList() {
		return locationList;
	}

	/**
	 * @param locationList
	 *            the locationList to set
	 */
	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

    @Override
    public String toString() {
        return String.format("LocationSearchFeed [locationList=%s]", locationList);
    }
}
