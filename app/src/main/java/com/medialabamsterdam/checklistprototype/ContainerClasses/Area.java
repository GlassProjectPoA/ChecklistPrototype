package com.medialabamsterdam.checklistprototype.ContainerClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 30/04/2015.
 */
public class Area implements Parcelable {

    public static final Parcelable.Creator<Area> CREATOR = new Parcelable.Creator<Area>() {
        public Area createFromParcel(Parcel in) {
            return new Area(in);
        }

        public Area[] newArray(int size) {
            return new Area[size];
        }
    };

    final private int id;
    final private String name;
    final private String topLeft;
    final private String botRight;
    final private int code;

    /**
     * Area default constructor.
     * <p>
     * Coordinates are stored as a string "##.######,##.######".
     * (latitude),(longitude)
     *
     * @param id       this Area's ID.
     * @param name     this Area's name.
     * @param topLeft  this Area's top-left coordinate.
     * @param botRight this Area's bottom-right coordinate.
     * @param code     this Area's code (2 numbers).
     */
    public Area(int id, String name, String topLeft, String botRight, int code) {
        this.id = id;
        this.name = name;
        this.topLeft = topLeft;
        this.botRight = botRight;
        this.code = code;
    }

    /**
     * Parcelable Constructor.
     *
     * @param in Parcel.
     */
    private Area(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.topLeft = in.readString();
        this.botRight = in.readString();
        this.code = in.readInt();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBotRight() {
        return botRight;
    }

    public String getTopLeft() {
        return topLeft;
    }

    public int getCode() {
        return code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeString(topLeft);
        out.writeString(botRight);
        out.writeInt(code);
    }

    @Override
    public String toString() {
        return "\nID: " + getId() +
                "\nNAME: " + getName() +
                "\nTOP_L: " + getTopLeft() +
                "\nBOT_R: " + getBotRight() +
                "\nCODE: " + getCode();
    }
}