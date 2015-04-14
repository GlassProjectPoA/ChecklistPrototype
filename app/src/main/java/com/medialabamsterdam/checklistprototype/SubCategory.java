package com.medialabamsterdam.checklistprototype;

/**
 * Created by Quintas on 14/04/2015.
 */
public class SubCategory {

    public int parentCategoryId;
    public String parentCategoryName;
    public int thisSubCategoryId;
    public String thisSubCategoryName;
    public int currentRating;

    public SubCategory(int parentCategoryId, String parentCategoryName, int thisSubCategoryId, String thisSubCategoryName, int currentRating) {
        this.parentCategoryId = parentCategoryId;
        this.parentCategoryName = parentCategoryName;
        this.thisSubCategoryId = thisSubCategoryId;
        this.thisSubCategoryName = thisSubCategoryName;
        this.currentRating = currentRating;
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
}
