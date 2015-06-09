package com.medialabamsterdam.checklistprototype.ContainerClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents a Location as stored on the Database.
 * <p>
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
    final private int locationId;
    final private String name;
    final private String topRight;
    final private String topLeft;
    final private String botLeft;
    final private String botRight;

    /**
     * Locations default constructor.
     * <p>
     * Coordinates are stored as a string "##.######,##.######".
     * (latitude),(longitude)
     *
     * @param locationId the ID of the Location.
     * @param name       the name of the Location.
     * @param topRight   the coordinate for the top-right corner of the location.
     * @param topLeft    the coordinate for the top-left corner of the location.
     * @param botLeft    the coordinate for the bottom-left corner of the location.
     * @param botRight   the coordinate for the bottom-right corner of the location.
     */
    public Locations(int locationId, String name, String topRight, String topLeft, String botLeft, String botRight) {
        this.locationId = locationId;
        this.name = name;
        this.topRight = topRight;
        this.topLeft = topLeft;
        this.botLeft = botLeft;
        this.botRight = botRight;
    }

    /**
     * Parcelable Constructor.
     *
     * @param in Parcel.
     */
    private Locations(Parcel in) {
        this.locationId = in.readInt();
        this.name = in.readString();
        this.topRight = in.readString();
        this.topLeft = in.readString();
        this.botLeft = in.readString();
        this.botRight = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(locationId);
        out.writeString(name);
        out.writeString(topRight);
        out.writeString(topLeft);
        out.writeString(botLeft);
        out.writeString(botRight);
    }

    //<editor-fold desc="Getters and Setters">
    public int getLocationId() {
        return locationId;
    }

    public String getName() {
        return name;
    }

    public String getTopRight() {
        return topRight;
    }

    public String getTopLeft() {
        return topLeft;
    }

    public String getBotLeft() {
        return botLeft;
    }

    public String getBotRight() {
        return botRight;
    }
    //</editor-fold>
}
