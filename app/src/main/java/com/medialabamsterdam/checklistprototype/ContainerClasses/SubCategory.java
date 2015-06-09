package com.medialabamsterdam.checklistprototype.ContainerClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents a SubCategory with all it's possible parameters.
 * <p>
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
    private int grade; // The actual given grade of the SubCategory. Is set to 0 when created.
    private String pictureUri;// Save the URI here in case the SubCategory rating is below accepted.
    private int code;

    /**
     * This constructor is only used to easily create a new card at the end of a CardScrollView.
     */
    public SubCategory() {
    }

    /**
     * SubCategory default constructor.
     *
     * @param parentId the Category's ID that references this SubCategory.
     * @param id       this SubCategory's  ID.
     * @param name     this SubCategory's Name.
     * @param remove   if this SubCategory should be removed from the listing.
     * @param code     this SubCategory's code (Area Code(2 numbers) + SubCategory Code(4 numbers))
     */
    public SubCategory(int parentId, int id, String name, boolean remove, int code) {
        this.parentId = parentId;
        this.id = id;
        this.name = name;
        this.remove = remove;
        this.grade = 1;
        this.code = code;
    }

    /**
     * Parcelable Constructor.
     *
     * @param in Parcel.
     */
    private SubCategory(Parcel in) {
        parentId = in.readInt();
        id = in.readInt();
        name = in.readString();
        remove = in.readByte() != 0;
        grade = in.readInt();
        pictureUri = in.readString();
        code = in.readInt();
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
        out.writeInt(grade);
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
                ", grade=" + grade +
                ", pictureUri='" + pictureUri + '\'' +
                '}';
    }

    public int getParentId() {
        return parentId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public boolean isRemove() {
        return remove;
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
}
