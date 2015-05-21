package com.medialabamsterdam.checklistprototype.ContainerClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents a Category with all it's possible parameters.
 * <p/>
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

    /**
     * Category default constructor.
     *
     * @param id                   this Category's ID
     * @param categoryByLocationId the ID that relates this Category to a Location.
     * @param name                 this Category's name.
     * @param remove               if this Category should be removed from the listing.
     */
    public Category(int id, int categoryByLocationId, String name, boolean remove) {
        this.id = id;
        this.categoryByLocationId = categoryByLocationId;
        this.name = name;
        this.remove = remove;
        this.completed = false; // Set to true if this Category has been fully graded.
    }

    /**
     * Parcelable Constructor.
     *
     * @param in Parcel.
     */
    public Category(Parcel in) {
        this.id = in.readInt();
        this.categoryByLocationId = in.readInt();
        this.name = in.readString();
        this.remove = in.readByte() != 0;
        this.completed = in.readByte() != 0;
    }

    /**
     * This constructor is only used to easily create a new card at the end of a CardScrollView.
     */
    public Category() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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
