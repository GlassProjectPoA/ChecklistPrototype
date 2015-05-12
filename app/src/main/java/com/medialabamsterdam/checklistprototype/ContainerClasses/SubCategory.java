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
    private int subCategoryId;
    private String subCategoryName;
    private boolean subCategoryRemove;
    private int currentRating;


    private SubCategory(Parcel in) {
        parentCategoryId = in.readInt();
        subCategoryId = in.readInt();
        subCategoryName = in.readString();
        subCategoryRemove = in.readByte()!= 0;
        currentRating = in.readInt();
    }

    public SubCategory() {}

    public SubCategory(int parentCategoryId, int subCategoryId, String subCategoryName, boolean subCategoryRemove) {
        this.parentCategoryId = parentCategoryId;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.subCategoryRemove = subCategoryRemove;
        this.currentRating = 0;
    }

    public int getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(int parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
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

    public boolean isSubCategoryRemove() {
        return subCategoryRemove;
    }

    public void setSubCategoryRemove(boolean subCategoryRemove) {
        this.subCategoryRemove = subCategoryRemove;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(parentCategoryId);
        out.writeInt(subCategoryId);
        out.writeString(subCategoryName);
        out.writeByte((byte) (subCategoryRemove ? 1 : 0));
        out.writeInt(currentRating);
    }

    @Override
    public String toString() {
        return "SubCategory{ " +
                "subCategoryId=" + subCategoryId +
                ", subCategoryName='" + subCategoryName + '\'' +
                ", currentRating=" + currentRating +
                ", subCategoryRemove=" + subCategoryRemove +
                '}';
    }
}
