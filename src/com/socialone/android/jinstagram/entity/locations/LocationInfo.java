package com.socialone.android.jinstagram.entity.locations;


import com.google.gson.annotations.SerializedName;
import com.socialone.android.jinstagram.InstagramObject;
import com.socialone.android.jinstagram.entity.common.Location;

public class LocationInfo extends InstagramObject {

	@SerializedName("data")
	private Location locationData;

	public Location getLocationData() {
		return locationData;
	}

	public void setLocationData(Location locationData) {
		this.locationData = locationData;
	}

    @Override
    public String toString() {
        return String.format("LocationInfo [locationData=%s]", locationData);
    }
}
