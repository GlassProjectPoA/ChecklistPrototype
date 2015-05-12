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
    private int id;
    private String name;
    private String topLeft;
    private String botRight;

    public Area(int id, String name, String topLeft, String botRight) {
        this.id = id;
        this.name = name;
        this.topLeft = topLeft;
        this.botRight = botRight;
    }

    private Area(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.topLeft = in.readString();
        this.botRight = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBotRight() {
        return botRight;
    }

    public void setBotRight(String botRight) {
        this.botRight = botRight;
    }

    public String getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(String topLeft) {
        this.topLeft = topLeft;
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
    }

    @Override
    public String toString() {
        return "\nID: " + getId() +
                "\nNAME: " + getName() +
                "\nTOP_L: " + getTopLeft() +
                "\nBOT_R: " + getBotRight();
    }
}