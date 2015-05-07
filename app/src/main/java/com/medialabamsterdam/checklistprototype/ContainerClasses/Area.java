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
    private int areaId;
    private String areaName;
    private String topLeft;
    private String botRight;

    public Area(int areaId, String areaName, String topLeft, String botRight) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.topLeft = topLeft;
        this.botRight = botRight;
    }

    private Area(Parcel in) {
        this.areaId = in.readInt();
        this.areaName = in.readString();
        this.topLeft = in.readString();
        this.botRight = in.readString();
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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
        out.writeInt(areaId);
        out.writeString(areaName);
        out.writeString(topLeft);
        out.writeString(botRight);
    }

    @Override
    public String toString(){
        return "ID: " + getAreaId() +
                "\nNAME: " + getAreaName() +
                "\nTOP_L: " + getTopLeft() +
                "\nBOT_R: " + getBotRight();
    }
}