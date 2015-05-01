package com.medialabamsterdam.checklistprototype.ContainerClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 21/04/2015.
 */
public class Locations implements Parcelable {

    public static final Parcelable.Creator<Locations> CREATOR = new Parcelable.Creator<Locations>() {
        public Locations createFromParcel(Parcel in) {
            return new Locations(in);
        }

        public Locations[] newArray(int size) {
            return new Locations[size];
        }
    };
    private int locationId;
    private String locationName;
    private String topRight;
    private String topLeft;
    private String botLeft;
    private String botRight;
    private ArrayList categories;

    public Locations(int locationId, String locationName, String topRight, String topLeft, String botLeft, String botRight) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.topRight = topRight;
        this.topLeft = topLeft;
        this.botLeft = botLeft;
        this.botRight = botRight;
    }

    public Locations(int locationId, String locationName, String topRight, String topLeft, String botLeft, String botRight, ArrayList categories) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.topRight = topRight;
        this.topLeft = topLeft;
        this.botLeft = botLeft;
        this.botRight = botRight;
        this.categories = categories;
    }

    public Locations(Parcel in) {
        this.locationId = in.readInt();
        this.locationName = in.readString();
        this.topRight = in.readString();
        this.topLeft = in.readString();
        this.botLeft = in.readString();
        this.botRight = in.readString();
        this.categories = in.readArrayList(Category.class.getClassLoader());

    }

    //<editor-fold desc="Getters and Setters">
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

    public String getTopRight() {
        return topRight;
    }

    public void setTopRight(String topRight) {
        this.topRight = topRight;
    }

    public String getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(String topLeft) {
        this.topLeft = topLeft;
    }

    public String getBotLeft() {
        return botLeft;
    }

    public void setBotLeft(String botLeft) {
        this.botLeft = botLeft;
    }

    public String getBotRight() {
        return botRight;
    }

    public void setBotRight(String botRight) {
        this.botRight = botRight;
    }

    public ArrayList getCategories() {
        return categories;
    }

    public void setCategories(ArrayList categories) {
        this.categories = categories;
    }
    //</editor-fold>

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(locationId);
        out.writeString(locationName);
        out.writeString(topRight);
        out.writeString(topLeft);
        out.writeString(botLeft);
        out.writeString(botRight);
        out.writeList(categories);
    }
}
