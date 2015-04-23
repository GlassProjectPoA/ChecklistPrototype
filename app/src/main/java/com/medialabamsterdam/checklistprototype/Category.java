package com.medialabamsterdam.checklistprototype;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Quintas on 21/04/2015.
 */
public class Category implements Parcelable {

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    private int categoryId;
    private String categoryName;
    private ArrayList subCategories;

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Category(int categoryId, String categoryName, ArrayList subCategories) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.subCategories = subCategories;
    }

    public Category(Parcel in) {
        this.categoryId = in.readInt();
        this.categoryName = in.readString();
        this.subCategories = in.readArrayList(SubCategory.class.getClassLoader());
    }

    public Category() {

    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList subCategories) {
        this.subCategories = subCategories;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(categoryId);
        out.writeString(categoryName);
        out.writeList(subCategories);
    }
}
