package com.medialabamsterdam.checklistprototype.Utilities;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 15/04/2015.
 */
public interface Constants {
    String EXTRA_POSITION = "com.medialabamsterdam.checklistprototype.POSITION";
    String EXTRA_GRADE = "com.medialabamsterdam.checklistprototype.GRADE";
    String EXTRA_GRADE_DETAIL = "com.medialabamsterdam.checklistprototype.GRADE_DETAIL";
    String EXTRA_PICTURE = "com.medialabamsterdam.checklistprototype.PICTURE";
    String EXTRA_CATEGORY = "com.medialabamsterdam.checklistprototype.CATEGORY";
    String EXTRA_CATEGORY_ID = "com.medialabamsterdam.checklistprototype.CATEGORY_ID";
    String EXTRA_SUBCATEGORY_ID = "com.medialabamsterdam.checklistprototype.SUBCATEGORY_ID";
    String EXTRA_CATEGORY_NAME = "com.medialabamsterdam.checklistprototype.CATEGORY_NAME";
    String EXTRA_SUBCATEGORY_NAME = "com.medialabamsterdam.checklistprototype.SUBCATEGORY_NAME";
    String EXTRA_SUBCATEGORY = "com.medialabamsterdam.checklistprototype.SUBCATEGORY";
    String EXTRA_LOCATION = "com.medialabamsterdam.checklistprototype.LOCATION";
    String EXTRA_AREA_CODE = "com.medialabamsterdam.checklistprototype.AREA_CODE";

    String PARCELABLE_SUBCATEGORY = "PARCELABLE_SUBCATEGORY";
    String PARCELABLE_CATEGORY = "PARCELABLE_CATEGORY";

    String WEB_SERVICE_URL = "http://glass-dev.twisk-interactive.nl/";
    int USER_ID = 1;

    /**
     * Settings.
     * Currently only works with "nl" as alternate language.
     */
    String ALTERNATE_LANGUAGE = "nl";
    boolean LOAD_ALTERNATE_LANGUAGE = false; // Set true if loading another language.
    boolean IGNORE_INSTRUCTIONS = true; // Set true to ignore instructions panel.
}
