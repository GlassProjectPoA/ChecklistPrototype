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

    private int parentId;
    private int id;
    private String name;
    private boolean remove;
    private int rating;
    private String pictureUri;
    private int code;


    private SubCategory(Parcel in) {
        parentId = in.readInt();
        id = in.readInt();
        name = in.readString();
        remove = in.readByte() != 0;
        rating = in.readInt();
        pictureUri = in.readString();
        code = in.readInt();
    }

    public SubCategory() {
    }

    public SubCategory(int parentId, int id, String name, boolean remove, int code) {
        this.parentId = parentId;
        this.id = id;
        this.name = name;
        this.remove = remove;
        this.rating = 0;
        this.code = code;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(parentId);
        out.writeInt(id);
        out.writeString(name);
        out.writeByte((byte) (remove ? 1 : 0));
        out.writeInt(rating);
        out.writeString(pictureUri);
        out.writeInt(code);
    }

    @Override
    public String toString() {
        return "SubCategory{ " +
                "parentId=" + parentId +
                ", id=" + id +
                ", code=" + code +
                ", name='" + name + '\'' +
                ", remove=" + remove +
                ", rating=" + rating +
                ", pictureUri='" + pictureUri + '\'' +
                '}';
    }
}
