package com.medialabamsterdam.checklistprototype.ContainerClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 14/04/2015.
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


    private int subCategoryId;
    private String subCategoryName;
    private boolean subCategoryRemove;
    private int currentRating;

    public SubCategory(int parentCategoryId, String parentCategoryName, int subCategoryId, String subCategoryName, int currentRating) {
        this.parentCategoryId = parentCategoryId;
        this.parentCategoryName = parentCategoryName;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.currentRating = currentRating;
    }


    private SubCategory(Parcel in) {
        parentCategoryId = in.readInt();
        parentCategoryName = in.readString();
        subCategoryId = in.readInt();
        subCategoryName = in.readString();
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

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
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
        out.writeInt(subCategoryId);
        out.writeString(subCategoryName);
        out.writeInt(currentRating);
    }
}
