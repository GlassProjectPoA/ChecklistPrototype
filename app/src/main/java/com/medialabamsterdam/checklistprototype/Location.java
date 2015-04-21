package com.medialabamsterdam.checklistprototype;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Quintas on 21/04/2015.
 */
public class Location implements Parcelable {

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
    private int locationId;
    private String locationName;
    private ArrayList categories;

    public Location(int locationId, String locationName) {
        this.locationId = locationId;
        this.locationName = locationName;
    }

    public Location(int locationId, String locationName, ArrayList categories) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.categories = categories;
    }

    public Location(Parcel in) {
        this.locationId = in.readInt();
        this.locationName = in.readString();
        this.categories = in.readArrayList(Category.class.getClassLoader());
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public ArrayList getCategories() {
        return categories;
    }

    public void setCategories(ArrayList categories) {
        this.categories = categories;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(locationId);
        out.writeString(locationName);
        out.writeList(categories);
    }
}
