package com.medialabamsterdam.checklistprototype;

/**
 * Created by Quintas on 14/04/2015.
 */
public class SubCategories {

    public int parentCategoryId;
    public String parentCategoryName;
    public int thisSubCategoryId;
    public String thisSubCategoryName;
    public int currentRating;

    public SubCategories(int parentCategoryId, String parentCategoryName, int thisSubCategoryId, String thisSubCategoryName, int currentRating) {
        this.parentCategoryId = parentCategoryId;
        this.parentCategoryName = parentCategoryName;
        this.thisSubCategoryId = thisSubCategoryId;
        this.thisSubCategoryName = thisSubCategoryName;
        this.currentRating = currentRating;
    }
}
