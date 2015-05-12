package com.medialabamsterdam.checklistprototype.ContainerClasses;

import android.os.Parcel;
import android.os.Parcelable;

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
    private int id;
    private int categoryByLocationId;
    private String name;
    private boolean remove;
    private boolean completed;

    public Category(int id, int categoryByLocationId, String name, boolean remove) {
        this.id = id;
        this.categoryByLocationId = categoryByLocationId;
        this.name = name;
        this.remove = remove;
        this.completed = false;
    }

    public Category(Parcel in) {
        this.id = in.readInt();
        this.categoryByLocationId = in.readInt();
        this.name = in.readString();
        this.remove = in.readByte() != 0;
        this.completed = in.readByte() != 0;
    }

    public Category() {
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

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getCategoryByLocationId() {
        return categoryByLocationId;
    }

    public void setCategoryByLocationId(int categoryByLocationId) {
        this.categoryByLocationId = categoryByLocationId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeInt(categoryByLocationId);
        out.writeString(name);
        out.writeByte((byte) (remove ? 1 : 0));
        out.writeByte((byte) (completed ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Category{ " +
                "id= " + id +
                ", categoryByLocationId= " + categoryByLocationId +
                ", name= '" + name + '\'' +
                ", remove= " + remove +
                ", completed= " + completed +
                '}';
    }
}
