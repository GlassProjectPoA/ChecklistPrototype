package com.medialabamsterdam.checklistprototype;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Quintas on 14/04/2015.
 */
public class SubCategory implements Parcelable {

    public static final Parcelable.Creator<SubCategory> CREATOR = new Parcelable.Creator<SubCategory>() {
        public SubCategory createFromParcel(Parcel in) {
            return new SubCategory(in);
        }

        public SubCategory[] newArray(int size) {
            return new SubCategory[size];
        }
    };
    private int parentCategoryId;
    private String parentCategoryName;
    private int thisSubCategoryId;
    private String thisSubCategoryName;
    private int currentRating;

    public SubCategory(int parentCategoryId, String parentCategoryName, int thisSubCategoryId, String thisSubCategoryName, int currentRating) {
        this.parentCategoryId = parentCategoryId;
        this.parentCategoryName = parentCategoryName;
        this.thisSubCategoryId = thisSubCategoryId;
        this.thisSubCategoryName = thisSubCategoryName;
        this.currentRating = currentRating;
    }


    private SubCategory(Parcel in) {
        parentCategoryId = in.readInt();
        parentCategoryName = in.readString();
        thisSubCategoryId = in.readInt();
        thisSubCategoryName = in.readString();
        currentRating = in.readInt();
    }

    public SubCategory() {

    }

    public int getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(int parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public int getThisSubCategoryId() {
        return thisSubCategoryId;
    }

    public void setThisSubCategoryId(int thisSubCategoryId) {
        this.thisSubCategoryId = thisSubCategoryId;
    }

    public String getThisSubCategoryName() {
        return thisSubCategoryName;
    }

    public void setThisSubCategoryName(String thisSubCategoryName) {
        this.thisSubCategoryName = thisSubCategoryName;
    }

    public int getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(int currentRating) {
        this.currentRating = currentRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(parentCategoryId);
        out.writeString(parentCategoryName);
        out.writeInt(thisSubCategoryId);
        out.writeString(thisSubCategoryName);
        out.writeInt(currentRating);
    }
}
