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
    private boolean categoryRemove;
    private ArrayList subCategories;

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryRemove = false;
    }

    public Category(int categoryId, String categoryName, boolean categoryRemove) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryRemove = categoryRemove;
    }

    public Category(int categoryId, String categoryName, boolean categoryRemove, ArrayList subCategories) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryRemove = categoryRemove;
        this.subCategories = subCategories;
    }

    public Category(Parcel in) {
        this.categoryId = in.readInt();
        this.categoryName = in.readString();
        this.categoryRemove = in.readByte()!= 0;
        this.subCategories = in.readArrayList(SubCategory.class.getClassLoader());
    }

    public Category(){}

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

    public boolean isCategoryRemove() {
        return categoryRemove;
    }

    public void setCategoryRemove(boolean categoryRemove) {
        this.categoryRemove = categoryRemove;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(categoryId);
        out.writeString(categoryName);
        out.writeByte((byte) (categoryRemove ? 1 : 0));
        out.writeList(subCategories);
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId= " + categoryId +
                ", categoryName= '" + categoryName + '\'' +
                ", categoryRemove= " + categoryRemove +
                '}';
    }
}
